

import org.gradle.api.Project

/**
 * dynamic bundle
 */
class DynamicBundlePlugin extends AndroidPlugin {

    @Override
    String mainPlugin() {
        return 'com.android.dynamic-feature'
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
            implementation project.project(project.gradle.ext.appModule.sourcePath)
            if (routerLibrary) {
                api routerLibrary
            }
            if ('ARouter' == routerName) {
                kapt 'com.alibaba:arouter-compiler:1.2.1'
            } else if ('WRouter' == routerName) {
                annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            }
        }
    }
}