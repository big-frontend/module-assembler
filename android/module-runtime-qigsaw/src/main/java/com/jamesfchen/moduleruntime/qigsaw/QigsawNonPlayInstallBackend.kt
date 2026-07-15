package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context
import com.jamesfchen.moduleruntime.ModuleError
import com.jamesfchen.moduleruntime.ModuleErrorCode
import com.jamesfchen.moduleruntime.ModuleInstallCallback
import com.jamesfchen.moduleruntime.ModuleState

interface QigsawInstallBridge {
    fun isReady(context: Context): Boolean

    fun isInstalled(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
    ): Boolean

    fun install(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
        listener: QigsawInstallListener,
    )
}

interface QigsawInstallListener {
    fun onDownloading()

    fun onInstalling()

    fun onInstalled()

    fun onFailed(
        code: Int,
        message: String,
        throwable: Throwable? = null,
    )
}

class QigsawNonPlayInstallBackend(
    private val bridge: QigsawInstallBridge,
) : NonPlayInstallBackend {
    override val backendId: String = "qigsaw"

    override fun isReady(context: Context): Boolean = bridge.isReady(context)

    override fun isInstalled(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
    ): Boolean {
        return bridge.isInstalled(context, moduleDefinition)
    }

    override fun install(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
        callback: ModuleInstallCallback,
    ) {
        callback.onStateChanged(ModuleState.Resolving)
        bridge.install(
            context = context,
            moduleDefinition = moduleDefinition,
            listener = object : QigsawInstallListener {
                override fun onDownloading() {
                    callback.onStateChanged(ModuleState.Downloading)
                }

                override fun onInstalling() {
                    callback.onStateChanged(ModuleState.Installing)
                }

                override fun onInstalled() {
                    callback.onCompleted(
                        channel = com.jamesfchen.moduleruntime.DistributionChannel.NON_PLAY,
                        installerId = backendId,
                    )
                }

                override fun onFailed(
                    code: Int,
                    message: String,
                    throwable: Throwable?,
                ) {
                    callback.onFailed(
                        ModuleError(
                            code = ModuleErrorCode.INSTALL_FAILED,
                            message = "Qigsaw install failed($code) for `${moduleDefinition.moduleName}`: $message",
                            cause = throwable,
                        )
                    )
                }
            }
        )
    }
}
