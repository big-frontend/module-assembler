package com.jamesfchen.moduleify

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jun/04/2021  Fri
 */
class ApiModulePlugin : Plugin<Project> {
    override fun apply(project: Project):Unit = project.run {
//        tasks {
//            register("greet") {
//                group = "sample"
//                description = "Prints a description of ${project.name}."
//                doLast {
//                    println("I'm ${project.name}.")
//                }
//            }
//        }
    }
//        project.apply(mapOf(
//                "plugin" to "com.android.library",
//                "plugin" to "kotlin-android"
//        ))
}