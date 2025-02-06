package com.electrolytej.assembler.module

import com.android.tools.idea.npw.module.ModuleDescriptionProvider
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.intellij.openapi.project.Project

class ModuleAssemblerModuleDescriptionProvider : ModuleDescriptionProvider {
    override fun getDescriptions(project: Project): Collection<ModuleGalleryEntry> {
        return emptyList()
    }

}