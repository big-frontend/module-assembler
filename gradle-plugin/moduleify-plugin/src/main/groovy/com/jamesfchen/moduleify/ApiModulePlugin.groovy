package com.jamesfchen.moduleify

import org.gradle.api.Project

class ApiModulePlugin extends BasePlugin {
    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
        project.plugins.apply('com.alibaba.arouter')
    }

    @Override
    void onApply(Project project) {
        project.dependencies {
            api 'io.github.jamesfchen:ibc-api:1.+'
            api 'com.alibaba:arouter-api:1.3.2'
        }
    }
}