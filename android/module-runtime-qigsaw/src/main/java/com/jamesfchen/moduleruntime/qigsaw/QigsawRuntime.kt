package com.jamesfchen.moduleruntime.qigsaw

import android.app.Application
import android.content.res.Resources
import com.iqiyi.android.qigsaw.core.Qigsaw
import com.iqiyi.android.qigsaw.core.SplitConfiguration
import java.util.concurrent.atomic.AtomicBoolean

object QigsawRuntime {
    private val installed = AtomicBoolean(false)
    @Volatile
    private var preloadSplitNames: List<String> = emptyList()

    @JvmStatic
    fun install(
        application: Application,
        catalog: NonPlayCatalog,
    ) {
        QigsawArtifactRegistry.registerCatalog(catalog)
        preloadSplitNames = catalog.definitions()
            .map { it.splitName }
            .distinct()
        if (installed.compareAndSet(false, true)) {
            Qigsaw.install(
                application,
                QigsawDownloader(),
                SplitConfiguration.newBuilder().build(),
            )
        }
    }

    @JvmStatic
    fun onApplicationCreated() {
        if (!installed.get()) return
        Qigsaw.onApplicationCreated()
        if (preloadSplitNames.isNotEmpty()) {
            Qigsaw.preloadInstalledSplits(preloadSplitNames)
        }
    }

    @JvmStatic
    fun onApplicationGetResources(resources: Resources): Resources {
        if (installed.get()) {
            Qigsaw.onApplicationGetResources(resources)
        }
        return resources
    }

    @JvmStatic
    fun isInstalled(): Boolean = installed.get()
}
