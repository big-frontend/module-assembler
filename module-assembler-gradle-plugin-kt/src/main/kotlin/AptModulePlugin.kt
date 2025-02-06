

import org.gradle.api.Project

class AptModulePlugin : JarModulePlugin() {
    override fun onApply(project: Project) {
//        project.dependencies {
//            project.implementation("com.squareup:javapoet:1.13.0")
//            project.implementation("com.squareup:kotlinpoet:1.7.2")
//        }
    }
}