/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.qihoo360.replugin.parasite2

import com.qihoo360.replugin.parasite2.debugger.PluginDebugger
import com.qihoo360.replugin.parasite2.inner.CommonData
import com.qihoo360.replugin.parasite2.inner.ReClassTransform
import com.qihoo360.replugin.util.VariantCompat
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author RePlugin Team
 */
public class ParasitePlugin implements Plugin<Project> {
    static boolean isSourcePath(def path) {
        return !path.contains(".")
    }

    @Override
    public void apply(Project project) {
        project.plugins.apply('com.android.application')
        project.ext {
            depsConfig = { configuration, simpleName ->
                def path = moduleify(simpleName)
                if (path) {
                    project.dependencies.add(configuration, path)
                }
            }
            moduleify = { simpleName ->
                def path = findModulePath(simpleName)
                if (path && isSourcePath(path)) {
                    path = project.rootProject.findProject(path)
                }
                return path
            }
            findModulePath = { simpleName ->
                if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
                    def module = project.gradle.ext.binaryModuleMap[simpleName]
                    if (module.binaryPath.isEmpty()) {
                        throw new IllegalArgumentException("binary module 的binaryPath不能为空")
                    }
                    return module.binaryPath //'com.jamesfchen:box-tool:1.0.0'
                } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
                    def module = project.gradle.ext.sourceModuleMap[simpleName]
                    if (module.sourcePath.isEmpty()) {
                        throw new IllegalArgumentException("source module 的sourcePath不能为空")
                    }
                    return module.sourcePath //':framework:common'
                }
                //该模块为exclude，不会进行编译不需要依赖
                return null
            }
            findModule = { simpleName ->
                if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.binaryModuleMap[simpleName]
                } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.sourceModuleMap[simpleName]
                }
                return null
            }
        }
        project.android {
            compileSdkVersion Integer.parseInt(project.rootProject.compileSdkVersion)
            buildToolsVersion project.rootProject.buildToolsVersion
            defaultConfig {
                //        multiDexEnabled = true//support android 20 or lower
                applicationId project.rootProject.applicationId
                minSdkVersion Integer.parseInt(project.rootProject.minSdkVersion)
                targetSdkVersion Integer.parseInt(project.rootProject.targetSdkVersion)
                def hited = project.rootProject.hasProperty("versionCode") || project.rootProject.hasProperty("versionName")
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
            signingConfigs {
                debugSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true

                }
                releaseSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true
                }
            }
            buildTypes {
                debug {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                    signingConfig signingConfigs.debugSigningConfig
                }
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                    signingConfig signingConfigs.releaseSigningConfig
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
        println "${AppConstant.TAG} Welcome to replugin world ! "

        /* Extensions */
        project.extensions.create(AppConstant.USER_CONFIG, ReClassConfig)
        project.afterEvaluate {
            if (!project.plugins.hasPlugin('com.android.application')) throw new IllegalArgumentException("replugin-parasite 插件必须在app模块配置")
            def config = project.extensions.getByName(AppConstant.USER_CONFIG)
            def forceStopHostAppTask = null
            def startHostAppTask = null
            def restartHostAppTask = null

            project.android.applicationVariants.all { variant ->
                PluginDebugger pluginDebugger = new PluginDebugger(project, config, variant)

                def assembleTask = VariantCompat.getAssembleTask(variant)
                def installPluginTask = project.tasks.create(AppConstant.TASK_INSTALL_PLUGIN)
                installPluginTask.doLast {
                    pluginDebugger.startHostApp()
                    pluginDebugger.uninstall()
                    pluginDebugger.forceStopHostApp()
                    pluginDebugger.startHostApp()
                    pluginDebugger.install()
                }
                installPluginTask.group = AppConstant.TASKS_GROUP

                def uninstallPluginTask = project.tasks.create(AppConstant.TASK_UNINSTALL_PLUGIN)

                uninstallPluginTask.doLast {
                    //generate json
                    pluginDebugger.uninstall()
                }
                uninstallPluginTask.group = AppConstant.TASKS_GROUP


                if (null == forceStopHostAppTask) {
                    forceStopHostAppTask = project.task(AppConstant.TASK_FORCE_STOP_HOST_APP)
                    forceStopHostAppTask.doLast {
                        //generate json
                        pluginDebugger.forceStopHostApp()
                    }
                    forceStopHostAppTask.group = AppConstant.TASKS_GROUP
                }

                if (null == startHostAppTask) {
                    startHostAppTask = project.task(AppConstant.TASK_START_HOST_APP)
                    startHostAppTask.doLast {
                        //generate json
                        pluginDebugger.startHostApp()
                    }
                    startHostAppTask.group = AppConstant.TASKS_GROUP
                }

                if (null == restartHostAppTask) {
                    restartHostAppTask = project.task(AppConstant.TASK_RESTART_HOST_APP)
                    restartHostAppTask.doLast {
                        //generate json
                        pluginDebugger.startHostApp()
                    }
                    restartHostAppTask.group = AppConstant.TASKS_GROUP
                    restartHostAppTask.dependsOn(forceStopHostAppTask)
                }


                if (assembleTask) {
                    installPluginTask.dependsOn assembleTask
                }

                def runPluginTask = project.tasks.create(AppConstant.TASK_RUN_PLUGIN)
                runPluginTask.doLast {
                    pluginDebugger.run()
                }
                runPluginTask.group = AppConstant.TASKS_GROUP

                def installAndRunPluginTask = project.tasks.create(AppConstant.TASK_INSTALL_AND_RUN_PLUGIN)
                installAndRunPluginTask.doLast {
                    pluginDebugger.run()
                }
                installAndRunPluginTask.group = AppConstant.TASKS_GROUP
                installAndRunPluginTask.dependsOn installPluginTask
            }
        }
        CommonData.appPackage = project.android.defaultConfig.applicationId

        println ">>> APP_PACKAGE " + CommonData.appPackage

        def transform = new ReClassTransform(project)
//         将 transform 注册到 android
        project.android.registerTransform(transform)
    }
}
