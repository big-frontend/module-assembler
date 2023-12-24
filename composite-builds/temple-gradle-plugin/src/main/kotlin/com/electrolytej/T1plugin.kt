package com.electrolytej

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import org.gradle.api.Project

class T1plugin :  Plugin<Project>  {

    override fun apply(project: Project) {
        println("T1plugin")
    }

}