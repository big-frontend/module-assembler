package com.jamesfchen.b

import org.gradle.api.Project

/**
 * native static bundle
 */
class NsBundlePlugin extends AndroidPlugin {

    @Override
    String mainPlugin() {
        return 'com.android.library'
    }

    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
//        if (routerType == TYPE_AROUTER) {
//            project.plugins.apply('com.alibaba.arouter')
//        }
//        project.plugins.apply('androidx.navigation.safeargs')
//        project.plugins.apply('androidx.navigation.safeargs.kotlin')
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        if ('ARouter' == routerName) {
            project['kapt'].arguments {
                arg("AROUTER_MODULE_NAME", project.getName())
            }
        }
        project.android {
//            buildFeatures {
//                viewBinding true
//                dataBinding true
//            }
        }
        project.dependencies {
            if (lifecycleVersion) {
                api "io.github.jamesfchen:lifecycle-api:$lifecycleVersion"
            }
            api routerLibrary
            if ('ARouter' == routerName) {
                kapt 'com.alibaba:arouter-compiler:1.2.1'
            } else if ('WRouter' == routerName) {
                annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            }
//            def nav_version = "2.3.5"
            if (navigationVersion) {
                api "androidx.navigation:navigation-fragment:$navigationVersion"
                api "androidx.navigation:navigation-runtime-ktx:$navigationVersion"
                api "androidx.navigation:navigation-common-ktx:$navigationVersion"
                api "androidx.navigation:navigation-ui:$navigationVersion"
                api "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
                api "androidx.navigation:navigation-ui-ktx:$navigationVersion"
//                api "androidx.navigation:navigation-compose:2.4.0-alpha02"
            }
        }
    }
}