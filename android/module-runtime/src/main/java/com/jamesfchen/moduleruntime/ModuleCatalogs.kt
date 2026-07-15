package com.jamesfchen.moduleruntime

class StaticModuleCatalog(
    descriptors: List<ModuleDescriptor>,
) : ModuleCatalog {
    private val modules = descriptors.associateBy { it.moduleName }

    override fun getModule(moduleName: String): ModuleDescriptor? = modules[moduleName]
}
