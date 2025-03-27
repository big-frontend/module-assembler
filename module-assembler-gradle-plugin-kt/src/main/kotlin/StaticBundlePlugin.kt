import org.gradle.api.Project
/**
 * static bundle
 */
class StaticBundlePlugin : AndroidPlugin() {
    override fun addPlugins(project: Project) {
    }
    override fun onApply( project:Project) {
        super.onApply(project)
        routerLibrary?.let {
            project.dependencies.add("implementation", it)
        }
    }

}