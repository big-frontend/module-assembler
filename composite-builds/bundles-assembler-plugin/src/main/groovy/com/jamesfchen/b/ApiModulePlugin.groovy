package com.jamesfchen.b

import org.gradle.api.Project

class ApiModulePlugin extends AndroidPlugin {
    @Override
    String mainPlugin() {
        return 'com.android.library'
    }
    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
//        project.plugins.apply(routerPlugin)
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.dependencies {
            api routerLibrary
        }
    }
}