import com.electrolytej.assembler.util.P
import org.gradle.api.Project

abstract class AndroidPlugin : BasePlugin() {
    companion object {
        var routerName: String = ""
        var routerPlugin = ""
        var routerLibrary = ""
        var isFirst = true
        fun pickupRouter(project: Project): Array<String> {
//            val AROUTER_VERSION: String? by project.rootProject
//            val WROUTER_VERSION: String? by project.rootProject
//            val IBC_VERSION: String? by project.rootProject
//
//            if (AROUTER_VERSION.isNullOrBlank() && WROUTER_VERSION.isNullOrBlank() && IBC_VERSION.isNullOrBlank()) {
//                throw IllegalArgumentException("四个只能选择一个")
//            } else if (IBC_VERSION.isNullOrBlank() && WROUTER_VERSION.isNullOrBlank()) {
//                throw IllegalArgumentException("两个只能选择一个")
//            } else if (AROUTER_VERSION.isNullOrBlank() && IBC_VERSION.isNullOrBlank()) {
//                throw IllegalArgumentException("两个只能选择一个")
//            } else if (WROUTER_VERSION.isNullOrBlank() && AROUTER_VERSION.isNullOrBlank()) {
//                throw IllegalArgumentException("两个只能选择一个")
//            }
//
//            val libs = project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
//            val versionName = libs.findVersion("ibc").get().requiredVersion
//            if (AROUTER_VERSION.isNullOrBlank()) return arrayOf("ARouter", "com.alibaba.arouter", "com.alibaba:arouter-api:${AROUTER_VERSION}")
//            if (WROUTER_VERSION.isNullOrBlank()) return arrayOf("WRouter", "WMRouter", "io.github.meituan-dianping:router:${WROUTER_VERSION}")
//            if (IBC_VERSION.isNullOrBlank()) return arrayOf("IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:${IBC_VERSION}")
            return arrayOf("", "", "")
        }
    }

    override fun addPlugins(project: Project) {
        if (isFirst) {
            val (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            if (routerLibrary.isBlank()) P.info("pick up router, $routerLibrary")
            isFirst = false
        }
//        project.plugins.apply("kotlin-android")
//        project.plugins.apply("kotlin-kapt")
    }

    override fun onApply(project: Project) {
//        project.android {
//            compileSdkVersion Integer . parseInt (project.rootProject.compileSdkVersion)
//            buildToolsVersion project . rootProject . buildToolsVersion defaultConfig {
//                minSdkVersion Integer . parseInt (project.rootProject.minSdkVersion)
//                targetSdkVersion Integer . parseInt (project.rootProject.targetSdkVersion)
//                def hited = project . rootProject . hasProperty ("versionCode") || project.rootProject.hasProperty(
//                    "versionName"
//                )
//                if (hited) {
//                    versionCode Integer . parseInt (project.rootProject.versionCode)
//                    versionName project . rootProject . versionName
//                }
//            }
//
//            def a = project . gradle . activeBuildVariant if (a != 'all' && a in project.gradle.ext.buildVariants) {
//                variantFilter { variant ->
//                    def flavors = variant . flavors *.name
//                    def buildType = variant . buildType . name if (!a.toLowerCase()
//                            .contains(buildType)
//                    ) {
//                        setIgnore(true)
//                    }
//                    for (def flavor in flavors) {
//                    if (!a.toLowerCase().contains(flavor)) {
//                        setIgnore(true)
//                    }
//                }
//                }
//            }
//        }
    }

}
