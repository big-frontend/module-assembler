package com.jamesfchen.moduleruntime.play

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.jamesfchen.moduleruntime.DistributionChannel
import com.jamesfchen.moduleruntime.ModuleDescriptor
import com.jamesfchen.moduleruntime.ModuleError
import com.jamesfchen.moduleruntime.ModuleErrorCode
import com.jamesfchen.moduleruntime.ModuleEnvironment
import com.jamesfchen.moduleruntime.ModuleInstallCallback
import com.jamesfchen.moduleruntime.ModuleInstaller
import com.jamesfchen.moduleruntime.ModuleState

class PlayFeatureModuleInstaller(
    private val splitInstallManager: SplitInstallManager,
) : ModuleInstaller {
    override val installerId: String = "play-core"

    override fun supports(
        moduleDescriptor: ModuleDescriptor,
        environment: ModuleEnvironment,
    ): Boolean {
        return environment.channel == DistributionChannel.PLAY &&
            moduleDescriptor.channelSupport.contains(DistributionChannel.PLAY)
    }

    override fun isInstalled(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
    ): Boolean {
        return splitInstallManager.installedModules.contains(moduleDescriptor.splitName)
    }

    override fun install(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
        callback: ModuleInstallCallback,
    ) {
        if (isInstalled(context, moduleDescriptor)) {
            callback.onCompleted(DistributionChannel.PLAY, installerId)
            return
        }
        callback.onStateChanged(ModuleState.Resolving)
        lateinit var listener: SplitInstallStateUpdatedListener
        listener = SplitInstallStateUpdatedListener { state ->
            if (!state.moduleNames().contains(moduleDescriptor.splitName)) {
                return@SplitInstallStateUpdatedListener
            }
            when (state.status()) {
                SplitInstallSessionStatus.PENDING -> callback.onStateChanged(ModuleState.Resolving)
                SplitInstallSessionStatus.DOWNLOADING -> callback.onStateChanged(ModuleState.Downloading)
                SplitInstallSessionStatus.DOWNLOADED -> callback.onStateChanged(ModuleState.Downloaded)
                SplitInstallSessionStatus.INSTALLING -> callback.onStateChanged(ModuleState.Installing)
                SplitInstallSessionStatus.INSTALLED -> {
                    splitInstallManager.unregisterListener(listener)
                    callback.onCompleted(DistributionChannel.PLAY, installerId)
                }

                SplitInstallSessionStatus.FAILED -> {
                    splitInstallManager.unregisterListener(listener)
                    callback.onFailed(
                        ModuleError(
                            code = ModuleErrorCode.INSTALL_FAILED,
                            message = "Play install failed for `${moduleDescriptor.moduleName}` with code ${state.errorCode()}.",
                        )
                    )
                }

                SplitInstallSessionStatus.CANCELED,
                SplitInstallSessionStatus.CANCELING,
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    splitInstallManager.unregisterListener(listener)
                    callback.onFailed(
                        ModuleError(
                            code = ModuleErrorCode.INSTALL_FAILED,
                            message = "Play install was interrupted for `${moduleDescriptor.moduleName}` with state ${state.status()}.",
                        )
                    )
                }
                else -> Unit
            }
        }
        splitInstallManager.registerListener(listener)
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleDescriptor.splitName)
            .build()
        splitInstallManager.startInstall(request)
            .addOnFailureListener { throwable ->
                splitInstallManager.unregisterListener(listener)
                callback.onFailed(
                    ModuleError(
                        code = ModuleErrorCode.INSTALL_FAILED,
                        message = "Play install request failed for `${moduleDescriptor.moduleName}`.",
                        cause = throwable,
                    )
                )
            }
    }
}

object PlayFeatureRuntime {
    @JvmStatic
    fun install(context: Context) {
        SplitCompat.install(context)
    }

    @JvmStatic
    fun installActivity(activity: android.app.Activity) {
        SplitCompat.installActivity(activity)
    }

    @JvmStatic
    fun createInstaller(context: Context): ModuleInstaller {
        return PlayFeatureModuleInstaller(
            splitInstallManager = SplitInstallManagerFactory.create(context.applicationContext)
        )
    }
}
