import org.gradle.api.Project

/**
 * dynamic bundle
 */
class DynamicBundlePlugin extends AndroidPlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.dynamic-feature')
    }

    @Override
    void onApply(Project project) {
        project.dependencies {
            implementation project.project(project.gradle.ext.appModule.sourcePath)
            if (routerLibrary) {
                api routerLibrary
            }
        }
    }
}