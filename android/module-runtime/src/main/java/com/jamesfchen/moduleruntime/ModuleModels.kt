package com.jamesfchen.moduleruntime

import android.os.Bundle

enum class DistributionChannel {
    PLAY,
    NON_PLAY,
}

enum class DeliveryType {
    INSTALL_TIME,
    ON_DEMAND,
}

enum class ModuleState {
    NotInstalled,
    Resolving,
    Downloading,
    Downloaded,
    Installing,
    Installed,
    Activating,
    Ready,
    Failed,
}

enum class ModuleErrorCode {
    MODULE_NOT_FOUND,
    CATALOG_NOT_FOUND,
    ENTRY_POINT_NOT_FOUND,
    NO_INSTALLER,
    INSTALL_FAILED,
    INSTALLER_NOT_READY,
    HOST_VERSION_TOO_LOW,
    LAUNCH_FAILED,
}

data class ModuleEnvironment(
    val channel: DistributionChannel,
    val installerPackageName: String? = null,
)

sealed class EntryPoint {
    data class Activity(val className: String) : EntryPoint()
}

data class ModuleDescriptor(
    val moduleName: String,
    val splitName: String,
    val deliveryType: DeliveryType,
    val channelSupport: Set<DistributionChannel>,
    val entryPoints: Map<String, EntryPoint>,
)

data class ModuleOpenRequest(
    val moduleName: String,
    val entryPoint: String,
    val extras: Bundle? = null,
)

data class ModuleError(
    val code: ModuleErrorCode,
    val message: String,
    val cause: Throwable? = null,
)

sealed class ModuleLaunchResult {
    data object Launched : ModuleLaunchResult()

    data class Failed(val error: ModuleError) : ModuleLaunchResult()
}

sealed class ModuleOpenResult {
    data class Opened(
        val channel: DistributionChannel,
        val installerId: String,
    ) : ModuleOpenResult()

    data class Failed(val error: ModuleError) : ModuleOpenResult()
}
