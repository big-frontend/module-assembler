package com.qihoo360

import com.liulishuo.okdownload.DownloadSerialQueue
import com.liulishuo.okdownload.DownloadTask
import com.qihoo360.replugin.RePlugin
import java.io.File


/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Aug/28/2022  Sun
 *
 *
 * 封装 插件/h5 js bundle/rn js bundle的下载 、 安装 、 加载
 */
object Installer {
    val parentFile: File = File("")
    fun install(pluginName: String): Boolean {
        val pluginInstalled = RePlugin.isPluginInstalled(pluginName)
        if (!pluginInstalled) {
            //网络下载
            val task = DownloadTask.Builder("", parentFile)
                .setFilename("haha") // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30) // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false).build()
            task.enqueue(object : DownloadSerialQueue() {
                override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                    super.fetchEnd(task, blockIndex, contentLength)
                    RePlugin.install("")
                }
            })

//            task.cancel()
//
//            task.execute(listener)
        }
        println("Hello, world!!!")

        return true
    }
}