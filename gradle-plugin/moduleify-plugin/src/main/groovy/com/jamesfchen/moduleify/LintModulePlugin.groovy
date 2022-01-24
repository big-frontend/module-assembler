package com.jamesfchen.moduleify

import org.gradle.api.Project

class LintModulePlugin extends JarModulePlugin {

    @Override
    protected String mainPlugin() {
        return 'java-library'
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.dependencies {
            //30.0.4
            compileOnly "com.android.tools.lint:lint-api:${rootProject.LINT_VERSION}"
            compileOnly "com.android.tools.lint:lint-checks:${rootProject.LINT_VERSION}"
        }

    }
}