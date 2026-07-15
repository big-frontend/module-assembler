package com.jamesfchen.moduleruntime

object ModuleRuntimeRegistry {
    @Volatile
    private var manager: ModuleManager? = null

    @JvmStatic
    fun install(moduleManager: ModuleManager) {
        manager = moduleManager
    }

    @JvmStatic
    fun get(): ModuleManager? = manager
}
