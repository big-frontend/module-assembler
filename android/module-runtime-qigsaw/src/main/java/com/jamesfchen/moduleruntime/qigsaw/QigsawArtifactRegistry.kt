package com.jamesfchen.moduleruntime.qigsaw

import java.util.concurrent.ConcurrentHashMap

internal data class QigsawArtifactMetadata(
    val sha256: String,
    val sizeBytes: Long,
)

internal object QigsawArtifactRegistry {
    private val artifactsByUrl = ConcurrentHashMap<String, QigsawArtifactMetadata>()

    fun registerCatalog(catalog: NonPlayCatalog) {
        artifactsByUrl.clear()
        catalog.definitions()
            .flatMap { it.artifacts }
            .forEach { artifact ->
                artifactsByUrl[artifact.url] = QigsawArtifactMetadata(
                    sha256 = artifact.sha256,
                    sizeBytes = artifact.sizeBytes,
                )
            }
    }

    fun findByUrl(url: String): QigsawArtifactMetadata? = artifactsByUrl[url]
}
