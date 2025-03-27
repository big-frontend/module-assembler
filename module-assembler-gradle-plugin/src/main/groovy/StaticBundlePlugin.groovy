import org.gradle.api.Project

/**
 * static bundle*/
class StaticBundlePlugin extends AndroidPlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
    }

    @Override
    void onApply(Project project) {
        project.dependencies {
            if (routerLibrary) {
                api routerLibrary
            }
        }
    }
}