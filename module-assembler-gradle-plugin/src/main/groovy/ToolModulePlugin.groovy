import org.gradle.api.Project

class ToolModulePlugin extends AndroidPlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
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