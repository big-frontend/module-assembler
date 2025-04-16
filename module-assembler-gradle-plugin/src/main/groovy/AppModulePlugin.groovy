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
            if (project.gradle.ext.dynamicModules) {
                dynamicFeatures = project.gradle.ext.dynamicModules
            }
            defaultConfig {
                def vc = libs.findVersion("versionCode").get().requiredVersion
                if (vc) {
                    versionCode Integer.parseInt(vc)
                }
                def vn = libs.findVersion("versionName").get().requiredVersion
                if (vn) {
                    versionName = vn
                }
            }

        }
    }
}