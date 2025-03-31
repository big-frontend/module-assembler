import org.gradle.api.Project

/**
 * dynamic bundle
 */
class DynamicBundlePlugin : BasePlugin() {
    override fun addPlugins(project: Project) {
        project.plugins.apply("com.android.dynamic-feature")
    }

    override fun onApply(project: Project) {
//            project.dependencies.add("implementation", project.project(project.gradle.ext.appModule.sourcePath))
        routerLibrary?.let {
            project.dependencies.add("implementation", it)
        }
    }
}