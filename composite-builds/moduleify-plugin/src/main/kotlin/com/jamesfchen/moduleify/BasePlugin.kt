package com.jamesfchen.moduleify

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/15/2022  Sat
 */
class BasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
//        extra["kotlin_version"] = "1.4.30"
    }
}