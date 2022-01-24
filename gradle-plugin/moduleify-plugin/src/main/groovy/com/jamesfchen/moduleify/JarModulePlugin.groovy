package com.jamesfchen.moduleify

import org.gradle.api.Project

abstract class JarModulePlugin extends BasePlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('kotlin')
    }
}