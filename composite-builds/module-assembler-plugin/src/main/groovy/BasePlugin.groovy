

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin implements Plugin<Project> {
    abstract protected String mainPlugin()
    abstract void addPlugins(Project project)
    abstract void onApply(Project project)
    @Override
    void apply(Project project) {
        project.plugins.apply(mainPlugin())
        addPlugins(project)
        project.ext {
            depsConfig = { configuration, simpleName ->
                def path = moduleify(simpleName)
                if (path) {
                    project.dependencies.add(configuration, path)
                }
            }
            moduleify = { simpleName ->
                def path = findModulePath(simpleName)
                if (path && isSourcePath(path)) {
                    path = project.rootProject.findProject(path)
                }
                return path
            }
            findModulePath = { simpleName ->
                if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
                    def module = project.gradle.ext.binaryModuleMap[simpleName]
                    if (module.binaryPath.isEmpty()) {
                        throw new IllegalArgumentException("binary module 的binaryPath不能为空")
                    }
                    return module.binaryPath //'com.jamesfchen:box-tool:1.0.0'
                } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
                    def module = project.gradle.ext.sourceModuleMap[simpleName]
                    if (module.sourcePath.isEmpty()) {
                        throw new IllegalArgumentException("source module 的sourcePath不能为空")
                    }
                    return module.sourcePath //':framework:common'
                }
                //该模块为exclude，不会进行编译不需要依赖
                return null
            }
            findModule = { simpleName ->
                if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.binaryModuleMap[simpleName]
                } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.sourceModuleMap[simpleName]
                }
                return null
            }
        }
        onApply(project)
    }
    static boolean isSourcePath(def path) {
        return !path.contains(".")
    }
}