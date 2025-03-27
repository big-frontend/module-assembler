import org.gradle.api.Project

/**
 * dynamic bundle
 */
class DynamicBundlePlugin : AndroidPlugin() {
    override fun addPlugins(project: Project) {
        project.plugins.apply("com.android.dynamic-feature")
    }

    override fun onApply(project: Project) {
        super.onApply(project)
//            project.dependencies.add("implementation", project.project(project.gradle.ext.appModule.sourcePath))
        routerLibrary?.let {
            project.dependencies.add("implementation", it)
        }
    }
}