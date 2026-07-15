package com.jamesfchen.moduleruntime.qigsaw

import android.app.Application
import com.jamesfchen.moduleruntime.ModuleInstaller

object AppDistributionInstallers {
    @JvmStatic
    fun create(application: Application): List<ModuleInstaller> {
        val catalog = AssetNonPlayCatalogLoader.loadOrEmpty(application)
        return listOf(
            NonPlayRuntime.createInstaller(
                catalog = catalog,
                backend = NonPlayBackendRegistry.getOrDefault(),
            )
        )
    }
}
