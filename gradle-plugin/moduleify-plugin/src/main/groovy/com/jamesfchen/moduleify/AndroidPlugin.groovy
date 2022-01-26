package com.jamesfchen.moduleify

import org.gradle.api.JavaVersion
import org.gradle.api.Project

abstract class AndroidPlugin extends BasePlugin {
    static String routerName
    static String routerPlugin
    static String routerLibrary
    static boolean isFirst = true
    static String navigationVersion
    static String lifecycleVersion

    @Override
    void addPlugins(Project project) {
        if (isFirst) {
            navigationVersion = project.rootProject.findProperty("NAVIGATION_VERSION")
            lifecycleVersion = project.rootProject.findProperty("LIFECYCLE_VERSION")
            (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            P.info("pick up router, $routerLibrary")
            isFirst = false
        }
        project.plugins.apply('kotlin-android')
        project.plugins.apply('kotlin-kapt')
    }

    @Override
    void onApply(Project project) {

        project.android {
            compileSdkVersion Integer.parseInt(project.rootProject.compileSdkVersion)
            buildToolsVersion project.rootProject.buildToolsVersion
            defaultConfig {
                minSdkVersion Integer.parseInt(project.rootProject.minSdkVersion)
                targetSdkVersion Integer.parseInt(project.rootProject.targetSdkVersion)
                def hited = project.rootProject.hasProperty("versionCode")
                        || project.rootProject.hasProperty("versionName")
                if (hited) {
                    versionCode Integer.parseInt(project.rootProject.versionCode)
                    versionName project.rootProject.versionName
                }
                testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }
            kotlinOptions {
                jvmTarget = "1.8"
            }
            buildTypes {
                debug {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
            }
            variantFilter { variant ->
                def flavors = variant.flavors*.name
                def buildType = variant.buildType.name
                def a = project.gradle.activeBuildVariant.toLowerCase()
                if (!a.contains(buildType)) {
                    setIgnore(true)
                }
                for (def flavor in flavors) {
                    if (!a.contains(flavor)) {
                        setIgnore(true)
                    }
                }

            }
        }
    }

    protected static def pickupRouter(Project project) {
        if (project.rootProject.findProperty("AROUTER_VERSION")
                && project.rootProject.findProperty("WROUTER_VERSION")
                && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("三个只能选择一个")
        } else if (project.rootProject.findProperty("IBCROUTER_VERSION") && project.rootProject.findProperty("WROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("AROUTER_VERSION") && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("WROUTER_VERSION") && project.rootProject.findProperty("AROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        }
        if (project.rootProject.findProperty("AROUTER_VERSION")) return ["ARouter", "com.alibaba.arouter", "com.alibaba:arouter-api:$project.rootProject.AROUTER_VERSION"]
        if (project.rootProject.findProperty("WROUTER_VERSION")) return ["WRouter", "WMRouter", "io.github.meituan-dianping:router:$project.rootProject.WROUTER_VERSION"]
        if (project.rootProject.findProperty("IBCROUTER_VERSION")) return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
        return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
    }
}
