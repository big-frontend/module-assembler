import com.electrolytej.assembler.util.P
import org.gradle.api.Project

abstract class AndroidPlugin : BasePlugin() {
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
