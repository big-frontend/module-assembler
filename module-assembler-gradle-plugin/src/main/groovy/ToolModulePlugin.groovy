import org.gradle.api.Project

class ToolModulePlugin extends AndroidPlugin {

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