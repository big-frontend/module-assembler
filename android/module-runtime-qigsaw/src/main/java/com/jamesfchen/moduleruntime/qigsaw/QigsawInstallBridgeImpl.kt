package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

internal class QigsawInstallBridgeImpl(
    context: Context,
) : QigsawInstallBridge {
    private val appContext = context.applicationContext
    private val splitInstallManager: SplitInstallManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SplitInstallManagerFactory.create(appContext)
    }

    override fun isReady(context: Context): Boolean = QigsawRuntime.isInstalled()

    override fun isInstalled(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
    ): Boolean {
        return splitInstallManager.installedModules.contains(moduleDefinition.splitName)
    }

    override fun install(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
        listener: QigsawInstallListener,
    ) {
        if (isInstalled(context, moduleDefinition)) {
            listener.onInstalled()
            return
        }
        lateinit var stateListener: SplitInstallStateUpdatedListener
        stateListener = SplitInstallStateUpdatedListener { state ->
            if (!state.moduleNames().contains(moduleDefinition.splitName)) {
                return@SplitInstallStateUpdatedListener
            }
            when (state.status()) {
                SplitInstallSessionStatus.PENDING,
                SplitInstallSessionStatus.DOWNLOADING,
                SplitInstallSessionStatus.DOWNLOADED,
                -> listener.onDownloading()

                SplitInstallSessionStatus.INSTALLING -> listener.onInstalling()

                SplitInstallSessionStatus.INSTALLED -> {
                    splitInstallManager.unregisterListener(stateListener)
                    listener.onInstalled()
                }

                SplitInstallSessionStatus.CANCELED,
                SplitInstallSessionStatus.CANCELING,
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
                SplitInstallSessionStatus.FAILED,
                -> {
                    splitInstallManager.unregisterListener(stateListener)
                    listener.onFailed(
                        code = state.errorCode(),
                        message = "Qigsaw session state ${state.status()} for ${moduleDefinition.splitName}",
                    )
                }
            }
        }
        splitInstallManager.registerListener(stateListener)
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleDefinition.splitName)
            .build()
        splitInstallManager.startInstall(request)
            .addOnFailureListener { throwable ->
                splitInstallManager.unregisterListener(stateListener)
                listener.onFailed(
                    code = -1,
                    message = "Failed to request install for ${moduleDefinition.splitName}",
                    throwable = throwable,
                )
            }
    }
}
