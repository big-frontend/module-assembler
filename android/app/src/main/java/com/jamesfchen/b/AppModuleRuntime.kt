package com.jamesfchen.b

import android.app.Application
import com.jamesfchen.moduleruntime.DeliveryType
import com.jamesfchen.moduleruntime.DistributionChannel
import com.jamesfchen.moduleruntime.EntryPoint
import com.jamesfchen.moduleruntime.IntentEntryPointLauncher
import com.jamesfchen.moduleruntime.ModuleDescriptor
import com.jamesfchen.moduleruntime.ModuleEnvironmentResolver
import com.jamesfchen.moduleruntime.ModuleManager
import com.jamesfchen.moduleruntime.ModuleRuntimeRegistry
import com.jamesfchen.moduleruntime.StaticModuleCatalog
import com.jamesfchen.moduleruntime.qigsaw.AppDistributionInstallers

object AppModuleRuntime {
    @JvmStatic
    fun initialize(application: Application) {
        val catalog = StaticModuleCatalog(
            descriptors = listOf(
                ModuleDescriptor(
                    moduleName = "im",
                    splitName = "bundle",
                    deliveryType = DeliveryType.ON_DEMAND,
                    channelSupport = setOf(DistributionChannel.PLAY, DistributionChannel.NON_PLAY),
                    entryPoints = mapOf(
                        "nav" to EntryPoint.Activity("com.example.compose.jetchat.NavActivity")
                    ),
                ),
                ModuleDescriptor(
                    moduleName = "hotel-h5",
                    splitName = "bundle4",
                    deliveryType = DeliveryType.ON_DEMAND,
                    channelSupport = setOf(DistributionChannel.PLAY, DistributionChannel.NON_PLAY),
                    entryPoints = mapOf(
                        "page" to EntryPoint.Activity("com.jamesfchen.bundle4.Bundle4Activity")
                    ),
                ),
            )
        )
        val manager = ModuleManager(
            catalog = catalog,
            installers = AppDistributionInstallers.create(application),
            launcher = IntentEntryPointLauncher(),
            environment = ModuleEnvironmentResolver.resolve(application),
        )
        ModuleRuntimeRegistry.install(manager)
    }
}
