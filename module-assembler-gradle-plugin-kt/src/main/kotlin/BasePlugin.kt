import com.electrolytej.assembler.util.P
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin : Plugin<Project> {
    companion object {
        var routerName: String? = null
        var routerPlugin: String? = null
        var routerLibrary: String? = null
        var isFirst = true
        val IBC_VERSION: String? = null
        fun pickupRouter(project: Project): Array<String> {
//            val libs = project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
//            val versionName = libs.findVersion("ibc").get().requiredVersion
            IBC_VERSION?.let {
                return@pickupRouter arrayOf(
                    "IBCRouter",
                    "io.github.jamesfchen.ibc-plugin",
                    "io.github.jamesfchen:ibc-api:${IBC_VERSION}"
                )
            }
            return arrayOf("", "", "")
        }
    }

    abstract fun addPlugins(project: Project)
    abstract fun onApply(project: Project)
    override fun apply(project: Project) {
        if (isFirst) {
            val (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            if (routerLibrary.isBlank()) P.info("pick up router, $routerLibrary")
            isFirst = false
        }

        onApply(project)
    }


}