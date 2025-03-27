import com.electrolytej.assembler.util.P
import com.electrolytej.assembler.util.ProjectUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import com.electrolytej.assembler.model.Module

class ModuleAssemblerRootProjectPlugin implements Plugin<Project>, ProjectEvaluationListener {
    @Override
    void apply(Project p) {
        def gradle = p.gradle
        gradle.ext.binaryModuleMap = new LinkedHashMap<String, Module>()
        gradle.ext.allModules.each { m ->
            m.binaryPath = "${gradle.ext.groupId}:${m.simpleName}:${ProjectUtil.newVersion(p, (Module)m)}"
            if (!gradle.ext.sourceModuleMap.containsKey(m.simpleName) && !gradle.ext.excludeModuleMap.containsKey(m.simpleName)) {
                gradle.ext.binaryModuleMap[m.simpleName] = (Module)m
            }
        }
        P.child("module info ========================================================================================")
        P.child("activeBuildVariant:" + gradle.ext.activeBuildVariant)
        P.child("module begin ========================================================================================")
        P.child("app    module\t${gradle.ext.appModule.sourcePath}")
        gradle.ext.sourceModuleMap.each { simpleName, module ->
            def name = ""
            if (module.format == "ndbundle") {
                name = "${module.sourcePath}(dynamic)"
            } else {
                name = module.sourcePath
            }
            P.child("source module\t$name")
        }
        gradle.ext.excludeModuleMap.each { _, module ->
            def name = ""
            if (module.format == "ndbundle") {
                name = "${module.sourcePath}(dynamic)"
            } else {
                name = module.sourcePath
            }
            P.child("${P.read("exclude module")}\t$name")
        }
        gradle.ext.binaryModuleMap.each { simpleName, module ->
            def name = ""
            if (module.format == "ndbundle") {
                name = "${module.binaryPath}(dynamic)"
            } else {
                name = module.binaryPath
            }
            P.child("${P.yellow("binary module")}\t$name")
        }

        P.child("module end ========================================================================================")
        gradle.addProjectEvaluationListener(this)
    }

    @Override
    void beforeEvaluate(Project project) {
//        if (!project.subprojects.isEmpty()) return
        project.ext {
            depsConfig = { configuration, simpleName ->
                ProjectUtil.depsConfig(project, configuration, simpleName)
            }
            moduleify = { simpleName ->
               return ProjectUtil.moduleify(project, simpleName)
            }
        }
    }

    @Override
    void afterEvaluate(Project project, ProjectState state) {
        //if (!project.subprojects.isEmpty())return
        //all模式下不发布组件
        if (project.gradle.ext.activeBuildVariant == 'all') return
        def theSimpleName = project.gradle.sourcePath2SimpleNameMap[project.path]
        if (theSimpleName) {
            def m = project.gradle.sourceModuleMap[theSimpleName]
            if (m.group == 'fwk' || m.format == 'nsbundle' || m.format == 'ndbundle') {
                project.plugins.apply("io.github.electrolytej.module-publisher-plugin")
                project['publish'].with {
                    name = theSimpleName
                    groupId = project.gradle.groupId
                    artifactId = theSimpleName
                    version = ProjectUtil.newVersion(project, (Module) m)
                    website = "https://github.com/big-frontend/module-assembler"
                }
            }
        }

    }

}

