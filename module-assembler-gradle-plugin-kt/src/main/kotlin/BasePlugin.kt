import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin : Plugin<Project> {
//    protected abstract fun mainPlugin(): String
    abstract fun addPlugins(project: Project)
    abstract fun onApply(project: Project)
    override fun apply(project: Project) {
//        project.plugins.apply(mainPlugin())
        addPlugins(project)

        onApply(project)
    }


}