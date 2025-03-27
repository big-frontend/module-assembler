import org.gradle.api.Project

class AppModulePlugin extends AndroidPlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.application')
        if (routerPlugin) project.plugins.apply(routerPlugin)
    }

    @Override
    void onApply(Project project) {
        project.android {
            defaultConfig {
//        multiDexEnabled = true//support android 20 or lower
                applicationId project.rootProject.applicationId
            }
            if (project.gradle.ext.dynamicModules){
                dynamicFeatures = project.gradle.ext.dynamicModules
            }
        }
    }
}