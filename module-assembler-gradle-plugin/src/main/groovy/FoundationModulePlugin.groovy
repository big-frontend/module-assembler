import org.gradle.api.Project

class FoundationModulePlugin extends AndroidPlugin {

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('com.android.library')
    }

    @Override
    void onApply(Project project) {

    }
}