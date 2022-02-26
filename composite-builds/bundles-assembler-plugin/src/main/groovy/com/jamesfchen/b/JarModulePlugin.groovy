package com.jamesfchen.b

import org.gradle.api.Project

class JarModulePlugin extends BasePlugin {

    @Override
    protected String mainPlugin() {
        return 'java-library'
    }

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('kotlin')
    }

    @Override
    void onApply(Project project) {

    }
}