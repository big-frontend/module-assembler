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

package com.qihoo360.replugin.plugin


import com.qihoo360.replugin.AdbCli
import com.qihoo360.replugin.Checker
import com.qihoo360.replugin.Constants
import com.qihoo360.replugin.VariantCompat
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.qihoo360.replugin.Util
/**
 * @author RePlugin Team
 */
class ParasitePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "${Constants.TAG} Welcome to replugin world ! "
        project.extensions.create(Constants.PARASITE_CONFIG, ParasiteConfig)
        project.afterEvaluate {
            if (!project.plugins.hasPlugin('com.android.application')) throw new IllegalArgumentException("pluginify-parasite 插件必须在app模块配置")
            def config = project.extensions.getByName(Constants.PARASITE_CONFIG)
            Checker.checkParasiteConfig(config)
            project.android.applicationVariants.all { variant ->
                def adb = AdbCli.create(project)
                File apkDir = new File(project.getBuildDir(), "outputs" + File.separator + "apk")
                def buildType = variant.buildType.name
                def apkName = ''
                variant.outputs.each {
                    apkName = it.outputFileName
                    println("cjf ${it.outputFileName}")
                }
                def flavor = variant.getFlavorName()
                def apkFile = new File("${apkDir}/$flavor/$buildType", apkName)

                def assembleTask = VariantCompat.getAssembleTask(variant)
                def installPluginTask = Util.createTask(project, Constants.TASK_INSTALL_PLUGIN) {
                    doLast {
                        adb.startHostApp("${config.hostApplicationId}/${config.hostAppLauncherActivity}")
                        adb.uninstallParasite(config.hostApplicationId, config.pluginName)
                        adb.forceStopHostApp(config.hostApplicationId)
                        adb.startHostApp("${config.hostApplicationId}/${config.hostAppLauncherActivity}")
                        adb.installParasite(config.hostApplicationId, config.pluginName, apkFile, onfig.phoneStorageDir)
                    }
                }

                if (assembleTask) {
                    installPluginTask.dependsOn assembleTask
                }

                def uninstallPluginTask = Util.createTask(project, Constants.TASK_UNINSTALL_PLUGIN) {
                    doLast {
                        //generate json
                        adb.uninstallParasite(config.hostApplicationId, config.pluginName)
                    }
                }

                def forceStopHostAppTask = Util.createTask(project, Constants.TASK_FORCE_STOP_HOST_APP) {
                    doLast {
                        adb.forceStopHostApp(config.hostApplicationId)
                        //generate json
                    }
                }

                def startHostAppTask = Util.createTask(project, Constants.TASK_START_HOST_APP) {
                    doLast {
                        adb.startHostApp("${config.hostApplicationId}/${config.hostAppLauncherActivity}")
                    }
                }

                def restartHostAppTask = Util.createTask(project, Constants.TASK_RESTART_HOST_APP) {
                    doLast {
                        //generate json
                        adb.startHostApp("${config.hostApplicationId}/${config.hostAppLauncherActivity}")
                    }
                }
                restartHostAppTask.dependsOn(forceStopHostAppTask)


                def runPluginTask = Util.createTask(project, Constants.TASK_RUN_PLUGIN) {
                    doLast {
                        adb.runParasite(config.hostApplicationId, config.pluginName)
                    }
                }
                def installAndRunPluginTask = Util.createTask(project, Constants.TASK_INSTALL_AND_RUN_PLUGIN) {
                    doLast {
                        adb.runParasite(config.hostApplicationId, config.pluginName)
                    }
                }
                installAndRunPluginTask.dependsOn installPluginTask
            }

            CommonData.appPackage = project.android.defaultConfig.applicationId

            println ">>> APP_PACKAGE " + CommonData.appPackage

            def transform = new ReClassTransform(project,config)
            // 将 transform 注册到 android
            project.android.registerTransform(transform)
        }
    }


}
