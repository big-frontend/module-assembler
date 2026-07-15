package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle

interface NonPlayBackendProvider {
    fun create(context: Context): NonPlayInstallBackend
}

object NonPlayRuntimeBootstrap {
    private const val META_DATA_PROVIDER =
        "module_runtime_nonplay_backend_provider"

    @JvmStatic
    fun install(context: Context) {
        val providerClassName = readProviderClassName(context) ?: return
        val provider = instantiateProvider(providerClassName) ?: return
        NonPlayBackendRegistry.install(provider.create(context.applicationContext))
    }

    private fun readProviderClassName(context: Context): String? {
        val appInfo = runCatching {
            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA,
            )
        }.getOrNull() ?: return null
        return appInfo.metaData.readProviderValue()
    }

    private fun Bundle?.readProviderValue(): String? {
        val value = this?.getString(META_DATA_PROVIDER)
        return value?.takeIf { it.isNotBlank() }
    }

    private fun instantiateProvider(className: String): NonPlayBackendProvider? {
        return runCatching {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            instance as? NonPlayBackendProvider
        }.getOrNull()
    }
}
