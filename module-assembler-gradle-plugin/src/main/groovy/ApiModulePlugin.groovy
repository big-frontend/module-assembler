

import org.gradle.api.Project

class ApiModulePlugin extends AndroidPlugin {
    @Override
    void addPlugins(Project project) {
        project.plugins.apply("com.android.library")
//        project.plugins.apply(routerPlugin)
    }

    @Override
    void onApply(Project project) {
        project.dependencies {
            if (routerLibrary) api routerLibrary
        }
    }
}