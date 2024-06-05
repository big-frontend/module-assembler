

import org.gradle.api.Project

/**
 * native static bundle
 */
class NsBundlePlugin extends AndroidPlugin {

    @Override
    String mainPlugin() {
        return 'com.android.library'
    }

    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        if ('ARouter' == routerName) {
            project['kapt'].arguments {
                arg("AROUTER_MODULE_NAME", project.getName())
            }
        }
        project.dependencies {
            if (routerLibrary) {
                if ('Navigation' == routerName) {
                    api "androidx.navigation:navigation-fragment:$routerLibrary"
                    api "androidx.navigation:navigation-runtime-ktx:$routerLibrary"
                    api "androidx.navigation:navigation-common-ktx:$routerLibrary"
                    api "androidx.navigation:navigation-ui:$routerLibrary"
                    api "androidx.navigation:navigation-fragment-ktx:$routerLibrary"
                    api "androidx.navigation:navigation-ui-ktx:$routerLibrary"
//                api "androidx.navigation:navigation-compose:2.4.0-alpha02"
                } else {
                    api routerLibrary
                }
            }
            if ('ARouter' == routerName) {
                kapt 'com.alibaba:arouter-compiler:1.2.1'
            } else if ('WRouter' == routerName) {
                annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            }
        }
    }
}