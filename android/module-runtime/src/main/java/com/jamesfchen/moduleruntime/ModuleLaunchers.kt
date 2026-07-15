package com.jamesfchen.moduleruntime

import android.content.Intent

class IntentEntryPointLauncher : ModuleLauncher {
    override fun launch(
        context: android.content.Context,
        moduleDescriptor: ModuleDescriptor,
        entryPoint: String,
        request: ModuleOpenRequest,
    ): ModuleLaunchResult {
        val target = moduleDescriptor.entryPoints[entryPoint]
            ?: return ModuleLaunchResult.Failed(
                ModuleError(
                    code = ModuleErrorCode.ENTRY_POINT_NOT_FOUND,
                    message = "Missing entry point `${request.entryPoint}` for `${request.moduleName}`.",
                )
            )
        return when (target) {
            is EntryPoint.Activity -> {
                val intent = Intent().setClassName(context, target.className)
                request.extras?.let(intent::putExtras)
                if (context !is android.app.Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                    ModuleLaunchResult.Launched
                } catch (throwable: Throwable) {
                    ModuleLaunchResult.Failed(
                        ModuleError(
                            code = ModuleErrorCode.LAUNCH_FAILED,
                            message = "Failed to launch `${target.className}`.",
                            cause = throwable,
                        )
                    )
                }
            }
        }
    }
}
