


import org.gradle.api.Project

class FoundationModulePlugin extends AndroidPlugin{
    @Override
    String mainPlugin() {
        return 'com.android.library'
    }
    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.android{
        }

    }
}