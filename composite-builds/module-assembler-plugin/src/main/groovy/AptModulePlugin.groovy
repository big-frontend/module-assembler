

import org.gradle.api.Project

class AptModulePlugin extends JarModulePlugin {
    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.dependencies {
            implementation 'com.squareup:javapoet:1.13.0'
            implementation 'com.squareup:kotlinpoet:1.7.2'
        }
    }
}