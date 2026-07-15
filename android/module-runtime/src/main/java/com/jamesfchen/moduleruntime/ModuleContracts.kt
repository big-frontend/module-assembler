package com.jamesfchen.moduleruntime

import android.content.Context

fun interface ModuleOpenCallback {
    fun onResult(result: ModuleOpenResult)
}

fun interface ModuleStateListener {
    fun onStateChanged(moduleName: String, state: ModuleState)
}

interface ModuleCatalog {
    fun getModule(moduleName: String): ModuleDescriptor?
}

interface ModuleLauncher {
    fun launch(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
        entryPoint: String,
        request: ModuleOpenRequest,
    ): ModuleLaunchResult
}

interface ModuleInstallCallback {
    fun onStateChanged(state: ModuleState) {}

    fun onCompleted(
        channel: DistributionChannel,
        installerId: String,
    )

    fun onFailed(error: ModuleError)
}

interface ModuleInstaller {
    val installerId: String

    fun supports(
        moduleDescriptor: ModuleDescriptor,
        environment: ModuleEnvironment,
    ): Boolean

    fun isInstalled(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
    ): Boolean

    fun install(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
        callback: ModuleInstallCallback,
    )
}
