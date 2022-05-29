package com.jamesfchen.b


import com.qihoo360.replugin.gradle.plugin.ReClassPlugin
import org.gradle.api.Project
/**
 * native dynamic bundle
 */
@Mixin(ReClassPlugin)
class NdBundlePlugin extends AndroidPlugin {
    def parasite = new ReClassPlugin()
    @Override
    String mainPlugin() {
        return 'com.android.application'
    }
    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
        if (lifecycleVersion) {
            project.plugins.apply('io.github.jamesfchen.lifecycle-plugin')
        }
        if (navigationVersion) {
            project.plugins.apply('androidx.navigation.safeargs')
        }
    }
    @Override
    void onApply(Project project) {
        super.onApply(project)
        parasite.apply(project)
        def sampleName = project.gradle.sourcePath2SimpleNameMap[project.path]
        project['repluginPluginConfig'].with {
            pluginName = sampleName
            hostApplicationId = project.rootProject.applicationId
        //launcher activity 有待优化
//            hostAppLauncherActivity = "com.jamesfchen.myhome.SplashActivity"
        }
        project.android {
            defaultConfig {
                applicationId project.rootProject.applicationId
            }
            signingConfigs {
                debugSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true

                }
                releaseSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true
                }
            }
            buildTypes {
                debug {
                    signingConfig signingConfigs.debugSigningConfig
                }
                release {
                    signingConfig signingConfigs.releaseSigningConfig
                }
            }
        }
        project.dependencies {
            if (lifecycleVersion) {
                implementation "io.github.jamesfchen:lifecycle-api:$lifecycleVersion"
            }
            implementation routerLibrary
            if (navigationVersion) {
                implementation "androidx.navigation:navigation-fragment:$navigationVersion"
                implementation "androidx.navigation:navigation-runtime-ktx:$navigationVersion"
                implementation "androidx.navigation:navigation-common-ktx:$navigationVersion"
                implementation "androidx.navigation:navigation-ui:$navigationVersion"
                implementation "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
                implementation "androidx.navigation:navigation-ui-ktx:$navigationVersion"
//                implementation "androidx.navigation:navigation-compose:$navigationVersion"
            }
        }
    }
}