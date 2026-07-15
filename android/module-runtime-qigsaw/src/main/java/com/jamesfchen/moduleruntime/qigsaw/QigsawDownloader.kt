package com.jamesfchen.moduleruntime.qigsaw

import com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader
import java.io.File
import java.io.InterruptedIOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class QigsawDownloader : Downloader {
    private val executor = Executors.newCachedThreadPool()
    private val sessionTasks = ConcurrentHashMap<Int, Future<*>>()

    override fun startDownload(
        sessionId: Int,
        requests: MutableList<DownloadRequest>,
        callback: DownloadCallback,
    ) {
        enqueue(sessionId, requests, callback)
    }

    override fun deferredDownload(
        sessionId: Int,
        requests: MutableList<DownloadRequest>,
        callback: DownloadCallback,
        usingMobileDataPermitted: Boolean,
    ) {
        enqueue(sessionId, requests, callback)
    }

    override fun cancelDownloadSync(sessionId: Int): Boolean {
        return sessionTasks.remove(sessionId)?.cancel(true) ?: false
    }

    override fun getDownloadSizeThresholdWhenUsingMobileData(): Long = -1L

    override fun isDeferredDownloadOnlyWhenUsingWifiData(): Boolean = false

    private fun enqueue(
        sessionId: Int,
        requests: MutableList<DownloadRequest>,
        callback: DownloadCallback,
    ) {
        val future = executor.submit {
            try {
                callback.onStart()
                if (requests.isEmpty()) {
                    callback.onCompleted()
                    return@submit
                }
                var completedBytes = 0L
                requests.forEach { request ->
                    ensureActive()
                    val downloadedSize = downloadRequest(
                        request = request,
                        completedBytes = completedBytes,
                        callback = callback,
                    )
                    completedBytes += downloadedSize
                }
                callback.onCompleted()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                callback.onCanceled()
            } catch (_: InterruptedIOException) {
                Thread.currentThread().interrupt()
                callback.onCanceled()
            } catch (_: DownloadCanceledException) {
                callback.onCanceled()
            } catch (throwable: Throwable) {
                callback.onError(ERROR_NETWORK)
            } finally {
                sessionTasks.remove(sessionId)
            }
        }
        sessionTasks[sessionId] = future
    }

    private fun downloadRequest(
        request: DownloadRequest,
        completedBytes: Long,
        callback: DownloadCallback,
    ): Long {
        val targetDir = File(request.fileDir).apply { mkdirs() }
        val targetFile = File(targetDir, request.fileName)
        val artifactMetadata = QigsawArtifactRegistry.findByUrl(request.url)
        if (targetFile.exists() && verifyDownloadedFile(targetFile, request, artifactMetadata)) {
            val size = targetFile.length()
            callback.onProgress(completedBytes + size)
            return size
        }
        targetFile.delete()
        val connection = (URL(request.url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 30_000
            instanceFollowRedirects = true
        }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("Unexpected HTTP ${connection.responseCode} for ${request.url}")
        }
        targetFile.outputStream().buffered().use { output ->
            connection.inputStream.buffered().use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var currentBytes = 0L
                while (true) {
                    ensureActive()
                    val read = input.read(buffer)
                    if (read < 0) break
                    output.write(buffer, 0, read)
                    currentBytes += read
                    callback.onProgress(completedBytes + currentBytes)
                }
            }
        }
        connection.disconnect()
        if (!verifyDownloadedFile(targetFile, request, artifactMetadata)) {
            targetFile.delete()
            throw IllegalStateException("Checksum mismatch for ${request.url}")
        }
        return targetFile.length()
    }

    private fun verifyDownloadedFile(
        file: File,
        request: DownloadRequest,
        metadata: QigsawArtifactMetadata?,
    ): Boolean {
        if (!file.exists()) return false
        if (metadata != null && metadata.sizeBytes > 0L && file.length() != metadata.sizeBytes) return false
        if (request.fileMD5.isNotBlank() && !file.hasDigest("MD5", request.fileMD5)) return false
        if (metadata != null && metadata.sha256.isNotBlank() && !file.hasDigest("SHA-256", metadata.sha256)) return false
        return true
    }

    private fun File.hasDigest(
        algorithm: String,
        expectedHex: String,
    ): Boolean {
        val digest = MessageDigest.getInstance(algorithm)
        inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        val actual = digest.digest().joinToString("") { "%02x".format(it) }
        return actual.equals(expectedHex, ignoreCase = true)
    }

    private fun ensureActive() {
        if (Thread.currentThread().isInterrupted) {
            throw DownloadCanceledException
        }
    }

    private data object DownloadCanceledException : RuntimeException()

    private companion object {
        const val ERROR_NETWORK = -1
    }
}
