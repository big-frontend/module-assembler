import org.gradle.api.Project
/**
 * static bundle
 */
class StaticBundlePlugin : BasePlugin() {
    override fun addPlugins(project: Project) {
    }
    override fun onApply( project:Project) {
        routerLibrary?.let {
            project.dependencies.add("implementation", it)
        }
    }

}