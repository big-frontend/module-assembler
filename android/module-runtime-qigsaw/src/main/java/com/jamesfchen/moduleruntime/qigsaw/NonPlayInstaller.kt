package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.jamesfchen.moduleruntime.DistributionChannel
import com.jamesfchen.moduleruntime.ModuleDescriptor
import com.jamesfchen.moduleruntime.ModuleEnvironment
import com.jamesfchen.moduleruntime.ModuleError
import com.jamesfchen.moduleruntime.ModuleErrorCode
import com.jamesfchen.moduleruntime.ModuleInstallCallback
import com.jamesfchen.moduleruntime.ModuleInstaller
import com.jamesfchen.moduleruntime.ModuleState

interface NonPlayInstallBackend {
    val backendId: String

    fun isReady(context: Context): Boolean

    fun isInstalled(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
    ): Boolean

    fun install(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
        callback: ModuleInstallCallback,
    )
}

class UnconfiguredNonPlayInstallBackend : NonPlayInstallBackend {
    override val backendId: String = "unconfigured"

    override fun isReady(context: Context): Boolean = false

    override fun isInstalled(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
    ): Boolean = false

    override fun install(
        context: Context,
        moduleDefinition: NonPlayModuleDefinition,
        callback: ModuleInstallCallback,
    ) {
        callback.onFailed(
            ModuleError(
                code = ModuleErrorCode.INSTALLER_NOT_READY,
                message = "Non-Play backend `${backendId}` is not configured for `${moduleDefinition.moduleName}`.",
            )
        )
    }
}

object NonPlayBackendRegistry {
    @Volatile
    private var backend: NonPlayInstallBackend? = null

    @JvmStatic
    fun install(backend: NonPlayInstallBackend) {
        this.backend = backend
    }

    @JvmStatic
    fun getOrDefault(): NonPlayInstallBackend {
        return backend ?: UnconfiguredNonPlayInstallBackend()
    }
}

class CatalogBackedNonPlayInstaller(
    private val catalog: NonPlayCatalog,
    private val backend: NonPlayInstallBackend,
) : ModuleInstaller {
    override val installerId: String = "nonplay-${backend.backendId}"

    override fun supports(
        moduleDescriptor: ModuleDescriptor,
        environment: ModuleEnvironment,
    ): Boolean {
        return environment.channel == DistributionChannel.NON_PLAY &&
            moduleDescriptor.channelSupport.contains(DistributionChannel.NON_PLAY) &&
            catalog.contains(moduleDescriptor.moduleName)
    }

    override fun isInstalled(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
    ): Boolean {
        val definition = catalog.get(moduleDescriptor.moduleName) ?: return false
        return backend.isInstalled(context, definition)
    }

    override fun install(
        context: Context,
        moduleDescriptor: ModuleDescriptor,
        callback: ModuleInstallCallback,
    ) {
        callback.onStateChanged(ModuleState.Resolving)
        val definition = catalog.get(moduleDescriptor.moduleName)
        if (definition == null) {
            callback.onFailed(
                ModuleError(
                    code = ModuleErrorCode.CATALOG_NOT_FOUND,
                    message = "Missing Non-Play catalog entry for `${moduleDescriptor.moduleName}`.",
                )
            )
            return
        }
        val hostVersionCode = context.packageManager.getPackageInfo(context.packageName, 0)
            .let(PackageInfoCompat::getLongVersionCode)
        if (hostVersionCode < definition.minHostVersionCode) {
            callback.onFailed(
                ModuleError(
                    code = ModuleErrorCode.HOST_VERSION_TOO_LOW,
                    message = "Host version $hostVersionCode is lower than required ${definition.minHostVersionCode} for `${definition.moduleName}`.",
                )
            )
            return
        }
        if (!backend.isReady(context)) {
            callback.onFailed(
                ModuleError(
                    code = ModuleErrorCode.INSTALLER_NOT_READY,
                    message = "Non-Play backend `${backend.backendId}` is not ready for `${definition.moduleName}`.",
                )
            )
            return
        }
        backend.install(context, definition, callback)
    }
}

object NonPlayRuntime {
    @JvmStatic
    fun createInstaller(
        catalog: NonPlayCatalog,
        backend: NonPlayInstallBackend = NonPlayBackendRegistry.getOrDefault(),
    ): ModuleInstaller {
        return CatalogBackedNonPlayInstaller(
            catalog = catalog,
            backend = backend,
        )
    }
}
