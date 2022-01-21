package com.jamesfchen.moduleify

import org.gradle.api.Project

class ApiModulePlugin extends BasePlugin {
    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
//        project.plugins.apply(routerPlugin)
    }

    @Override
    void onApply(Project project) {
        project.dependencies {
            api routerLibrary
        }
    }
}