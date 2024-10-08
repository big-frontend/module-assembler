import com.electrolytej.b.P
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

abstract class AndroidPlugin extends BasePlugin {
    static String routerName
    static String routerPlugin
    static String routerLibrary
    static boolean isFirst = true

    @Override
    void addPlugins(Project project) {
        if (isFirst) {
            (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            if (routerLibrary) P.info("pick up router, $routerLibrary")
            isFirst = false
        }
        project.plugins.apply('kotlin-android')
        project.plugins.apply('kotlin-kapt')
    }

    @Override
    void onApply(Project project) {
        project.android {
            compileSdkVersion Integer.parseInt(project.rootProject.compileSdkVersion)
            buildToolsVersion project.rootProject.buildToolsVersion
            defaultConfig {
                minSdkVersion Integer.parseInt(project.rootProject.minSdkVersion)
                targetSdkVersion Integer.parseInt(project.rootProject.targetSdkVersion)
                def hited = project.rootProject.hasProperty("versionCode") || project.rootProject.hasProperty("versionName")
                if (hited) {
                    versionCode Integer.parseInt(project.rootProject.versionCode)
                    versionName project.rootProject.versionName
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
    }

    protected static def pickupRouter(Project project) {
        if (project.rootProject.findProperty("AROUTER_VERSION")
                && project.rootProject.findProperty("WROUTER_VERSION")
                && project.rootProject.findProperty("IBC_VERSION")) {
            throw new IllegalArgumentException("四个只能选择一个")
        } else if (project.rootProject.findProperty("IBC_VERSION") && project.rootProject.findProperty("WROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("AROUTER_VERSION") && project.rootProject.findProperty("IBC_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("WROUTER_VERSION") && project.rootProject.findProperty("AROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        }

        VersionCatalog libs = project.extensions.getByType(VersionCatalogsExtension).named("libs")
        def versionName = libs.findVersion("ibc").get().requiredVersion
        if (project.rootProject.findProperty("AROUTER_VERSION")) return ["ARouter", "com.alibaba.arouter", "com.alibaba:arouter-api:$project.rootProject.AROUTER_VERSION"]
        if (project.rootProject.findProperty("WROUTER_VERSION")) return ["WRouter", "WMRouter", "io.github.meituan-dianping:router:$project.rootProject.WROUTER_VERSION"]
        if (project.rootProject.findProperty("IBC_VERSION")) return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBC_VERSION"]
        return ["", "", ""]
    }
}
