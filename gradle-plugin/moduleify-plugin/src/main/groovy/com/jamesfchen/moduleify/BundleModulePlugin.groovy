package com.jamesfchen.moduleify


import org.gradle.api.Project

class BundleModulePlugin extends BasePlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
//        if (routerType == TYPE_AROUTER) {
//            project.plugins.apply('com.alibaba.arouter')
//        }
//        project.plugins.apply('androidx.navigation.safeargs')
//        project.plugins.apply('androidx.navigation.safeargs.kotlin')
    }

    @Override
    void onApply(Project project) {
        project['kapt'].arguments {
            arg("AROUTER_MODULE_NAME", project.getName())
        }
        project.android {
//            buildFeatures {
//                viewBinding true
//                dataBinding true
//            }
        }
        project.dependencies {
            api routerLibrary
            if (lifecycleVersion){
                api "io.github.jamesfchen:lifecycle-api:$lifecycleVersion"
            }
            kapt 'com.alibaba:arouter-compiler:1.2.1'
            annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            project.gradle.framworkApiModuleMap.each { simpleName, m ->
                def path = project.moduleify(simpleName)
                implementation path
            }
//            def nav_version = "2.3.5"
            if (navigationVersion) {
                api "androidx.navigation:navigation-fragment:$navigationVersion"
                api "androidx.navigation:navigation-ui:$navigationVersion"
                api "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
                api "androidx.navigation:navigation-ui-ktx:$navigationVersion"
                api "androidx.navigation:navigation-dynamic-features-fragment:$navigationVersion"
                api "androidx.navigation:navigation-compose:2.4.0-alpha02"
            }
        }
    }
}