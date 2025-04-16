import com.electrolytej.assembler.util.P
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

abstract class AndroidPlugin implements Plugin<Project> {
    static String routerPlugin
    static String routerLibrary
    static boolean isFirst = true
    protected VersionCatalog libs

    abstract void addPlugins(Project project)

    abstract void onApply(Project project)

    @Override
    void apply(Project project) {
        libs = project.extensions.getByType(VersionCatalogsExtension).named("libs")
        addPlugins(project)
        def useIbc = project.findProperty("useIbc")
        if (isFirst && useIbc) {
//        project.pluginManager.apply("io.github.jamesfchen.ibc-plugin:${ibc}")
            def ibc = "1.6.1"
//                routerPlugin = "io.github.jamesfchen.ibc-plugin"
//                routerLibrary = "io.github.jamesfchen:ibc-api:${ibc}"
            P.info("pick up router, $routerLibrary")
            isFirst = false
        }
        project.android {
            def compile = libs.findVersion("compileSdkVersion").get().requiredVersion
            def build = libs.findVersion("buildToolsVersion").get().requiredVersion
            if (compile) {
                compileSdkVersion Integer.parseInt(compile)
            }
            if (build) {
                buildToolsVersion build
            }
            defaultConfig {
                def min = libs.findVersion("minSdkVersion").get().requiredVersion
                def target = libs.findVersion("targetSdkVersion").get().requiredVersion
                if (min) {
                    minSdkVersion = min
                }
                if (target) {
                    targetSdkVersion = target
                }
            }
            flavorDimensions "device"
            productFlavors {
                tv {
                    dimension 'device'
                }
                watch {
                    dimension 'device'
                }
            }
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
