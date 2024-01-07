package com.electrolytej

import org.gradle.api.Plugin
import org.gradle.api.Project

class T1plugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("T1plugin")
    }

}