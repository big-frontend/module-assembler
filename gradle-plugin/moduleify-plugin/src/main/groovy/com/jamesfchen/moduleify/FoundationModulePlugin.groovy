package com.jamesfchen.moduleify


import org.gradle.api.Project

class FoundationModulePlugin extends BasePlugin{
    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
    }

    @Override
    void onApply(Project project) {
        project.android{
        }

    }
}