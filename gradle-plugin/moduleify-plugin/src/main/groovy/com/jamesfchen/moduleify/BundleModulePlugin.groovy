package com.jamesfchen.moduleify


import org.gradle.api.Project

class BundleModulePlugin extends BasePlugin {
    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
        project.plugins.apply('com.alibaba.arouter')
    }

    @Override
    void onApply(Project project) {
        project['kapt'].arguments {
            arg("AROUTER_MODULE_NAME", project.getName())
        }
        project.android {
            buildFeatures {
                viewBinding true
                dataBinding true
            }
        }
        project.dependencies {
            api 'io.github.jamesfchen:ibc-api:1.2.0'
            api 'com.alibaba:arouter-api:1.3.2'
            kapt 'com.alibaba:arouter-compiler:1.2.1'
            annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            project.gradle.framworkApiModuleMap.each { simpleName, m ->
                def path = project.moduleify(simpleName)
                implementation path

            }
        }
    }
}