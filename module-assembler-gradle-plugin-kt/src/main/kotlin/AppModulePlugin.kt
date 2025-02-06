import org.gradle.api.Project

class AppModulePlugin : AndroidPlugin() {

    override fun addPlugins(project: Project) {
        super.addPlugins(project)
        if (routerPlugin.isNotEmpty()) project.plugins.apply(routerPlugin)
    }

    override fun onApply(project: Project) {
        super.onApply(project)
//        project.android {
//            defaultConfig {
////        multiDexEnabled = true//support android 20 or lower
//                applicationId project . rootProject . applicationId
//            }
//            signingConfigs {
//                debugSigningConfig {
//                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
//                    storeFile project . file ("$project.rootDir/$project.rootProject.storeFilePath")
//                    v1SigningEnabled true
//                    v2SigningEnabled true
//
//                }
//                releaseSigningConfig {
//                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
//                    storeFile project . file ("$project.rootDir/$project.rootProject.storeFilePath")
//                    v1SigningEnabled true
//                    v2SigningEnabled true
//                }
//
//            }
//            buildTypes {
//                release {
//                    signingConfig signingConfigs . releaseSigningConfig
//                }
//                debug {
//                    signingConfig signingConfigs . debugSigningConfig
//                }
//            }
//            if (project.gradle.ext.dynamicModules) {
//                dynamicFeatures = project.gradle.ext.dynamicModules
//            }
//        }
    }
}