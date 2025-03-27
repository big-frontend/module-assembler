import com.electrolytej.assembler.util.P
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

abstract class AndroidPlugin implements Plugin<Project> {
    static String routerName
    static String routerPlugin
    static String routerLibrary
    static boolean isFirst = true

    abstract void addPlugins(Project project)

    abstract void onApply(Project project)

    static def pickupRouter(Project project) {
        VersionCatalog libs = project.extensions.getByType(VersionCatalogsExtension).named("libs")
        def versionName = libs.findVersion("ibc").get().requiredVersion
        if (project.rootProject.findProperty("IBC_VERSION")) return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBC_VERSION"]
        return ["", "", ""]
    }

    @Override
    void apply(Project project) {
        if (isFirst) {
            (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            if (routerLibrary) P.info("pick up router, $routerLibrary")
            isFirst = false
        }
        addPlugins(project)
        project.android {
            def a = project.gradle.activeBuildVariant
            if (a != 'all' && a in project.gradle.ext.buildVariants) {
                variantFilter { variant ->
                    def flavors = variant.flavors*.name
                    def buildType = variant.buildType.name
                    if (!a.toLowerCase().contains(buildType)) {
                        setIgnore(true)
                    }
                    for (def flavor in flavors) {
                        if (!a.toLowerCase().contains(flavor)) {
                            setIgnore(true)
                        }
                    }
                }
            }
        }
        onApply(project)
    }

}
