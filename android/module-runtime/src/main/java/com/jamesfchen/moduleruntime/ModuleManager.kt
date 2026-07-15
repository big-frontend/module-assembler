package com.jamesfchen.moduleruntime

import android.content.Context
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class ModuleManager(
    private val catalog: ModuleCatalog,
    private val installers: List<ModuleInstaller>,
    private val launcher: ModuleLauncher,
    private val environment: ModuleEnvironment,
) {
    private val moduleStates = ConcurrentHashMap<String, ModuleState>()
    private val listeners = CopyOnWriteArraySet<ModuleStateListener>()

    fun addStateListener(listener: ModuleStateListener) {
        listeners.add(listener)
    }

    fun removeStateListener(listener: ModuleStateListener) {
        listeners.remove(listener)
    }

    fun getModuleState(moduleName: String): ModuleState {
        return moduleStates[moduleName] ?: ModuleState.NotInstalled
    }

    fun openModule(
        context: Context,
        request: ModuleOpenRequest,
        callback: ModuleOpenCallback,
    ) {
        updateState(request.moduleName, ModuleState.Resolving)
        val descriptor = catalog.getModule(request.moduleName)
        if (descriptor == null) {
            updateState(request.moduleName, ModuleState.Failed)
            callback.onResult(
                ModuleOpenResult.Failed(
                    ModuleError(
                        code = ModuleErrorCode.MODULE_NOT_FOUND,
                        message = "Unknown module `${request.moduleName}`.",
                    )
                )
            )
            return
        }
        val installer = installers.firstOrNull { it.supports(descriptor, environment) }
        if (installer == null) {
            updateState(request.moduleName, ModuleState.Failed)
            callback.onResult(
                ModuleOpenResult.Failed(
                    ModuleError(
                        code = ModuleErrorCode.NO_INSTALLER,
                        message = "No installer available for `${descriptor.moduleName}` on `${environment.channel}`.",
                    )
                )
            )
            return
        }
        if (installer.isInstalled(context, descriptor)) {
            updateState(request.moduleName, ModuleState.Installed)
            launchInstalledModule(
                context = context,
                descriptor = descriptor,
                request = request,
                installerId = installer.installerId,
                channel = environment.channel,
                callback = callback,
            )
            return
        }
        installer.install(
            context = context,
            moduleDescriptor = descriptor,
            callback = object : ModuleInstallCallback {
                override fun onStateChanged(state: ModuleState) {
                    updateState(request.moduleName, state)
                }

                override fun onCompleted(
                    channel: DistributionChannel,
                    installerId: String,
                ) {
                    updateState(request.moduleName, ModuleState.Installed)
                    launchInstalledModule(
                        context = context,
                        descriptor = descriptor,
                        request = request,
                        installerId = installerId,
                        channel = channel,
                        callback = callback,
                    )
                }

                override fun onFailed(error: ModuleError) {
                    updateState(request.moduleName, ModuleState.Failed)
                    callback.onResult(ModuleOpenResult.Failed(error))
                }
            }
        )
    }

    private fun launchInstalledModule(
        context: Context,
        descriptor: ModuleDescriptor,
        request: ModuleOpenRequest,
        installerId: String,
        channel: DistributionChannel,
        callback: ModuleOpenCallback,
    ) {
        updateState(request.moduleName, ModuleState.Activating)
        when (
            val launchResult = launcher.launch(
                context = context,
                moduleDescriptor = descriptor,
                entryPoint = request.entryPoint,
                request = request,
            )
        ) {
            ModuleLaunchResult.Launched -> {
                updateState(request.moduleName, ModuleState.Ready)
                callback.onResult(
                    ModuleOpenResult.Opened(
                        channel = channel,
                        installerId = installerId,
                    )
                )
            }

            is ModuleLaunchResult.Failed -> {
                updateState(request.moduleName, ModuleState.Failed)
                callback.onResult(ModuleOpenResult.Failed(launchResult.error))
            }
        }
    }

    private fun updateState(
        moduleName: String,
        state: ModuleState,
    ) {
        moduleStates[moduleName] = state
        listeners.forEach { it.onStateChanged(moduleName, state) }
    }
}
