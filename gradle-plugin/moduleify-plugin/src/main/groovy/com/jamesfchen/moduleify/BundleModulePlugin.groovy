package com.jamesfchen.moduleify


import org.gradle.api.Project

class BundleModulePlugin extends BasePlugin {
    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
        project.plugins.apply('com.alibaba.arouter')
        project.plugins.apply('androidx.navigation.safeargs')
//        project.plugins.apply('androidx.navigation.safeargs.kotlin')
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
            api "io.github.jamesfchen:lifecycle-api:1.0.2"
            api 'io.github.jamesfchen:ibc-api:1.2.0'
            api 'com.alibaba:arouter-api:1.3.2'
            api 'io.github.meituan-dianping:router:1.2.1'
            kapt 'com.alibaba:arouter-compiler:1.2.1'
            annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            project.gradle.framworkApiModuleMap.each { simpleName, m ->
                def path = project.moduleify(simpleName)
                implementation path
            }
            def nav_version = "2.3.5"

            api "androidx.navigation:navigation-fragment:$nav_version"
            api "androidx.navigation:navigation-ui:$nav_version"
            api "androidx.navigation:navigation-fragment-ktx:$nav_version"
            api "androidx.navigation:navigation-ui-ktx:$nav_version"
            api "androidx.navigation:navigation-dynamic-features-fragment:$nav_version"
            androidTestImplementation "androidx.navigation:navigation-testing:$nav_version"

            // Jetpack Compose Integration
            api "androidx.navigation:navigation-compose:2.4.0-alpha02"
        }
    }
}