package com.jamesfchen.moduleruntime

import android.content.Context
import android.os.Build

object ModuleEnvironmentResolver {
    fun resolve(context: Context): ModuleEnvironment {
        val installerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            }.getOrNull()
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }
        val channel = if (installerPackageName == "com.android.vending") {
            DistributionChannel.PLAY
        } else {
            DistributionChannel.NON_PLAY
        }
        return ModuleEnvironment(
            channel = channel,
            installerPackageName = installerPackageName,
        )
    }
}
