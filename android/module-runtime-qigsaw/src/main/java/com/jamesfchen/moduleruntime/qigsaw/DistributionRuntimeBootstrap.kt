package com.jamesfchen.moduleruntime.qigsaw

import android.app.Application
import android.content.res.Resources

object DistributionRuntimeBootstrap {
    @JvmStatic
    fun attachBaseContext(application: Application) {
        val catalog = AssetNonPlayCatalogLoader.loadOrEmpty(application)
        QigsawRuntime.install(application, catalog)
        NonPlayRuntimeBootstrap.install(application)
    }

    @JvmStatic
    fun onCreate(application: Application) {
        QigsawRuntime.onApplicationCreated()
    }

    @JvmStatic
    fun getResources(resources: Resources): Resources {
        return QigsawRuntime.onApplicationGetResources(resources)
    }
}
