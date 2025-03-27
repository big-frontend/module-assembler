import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import com.electrolytej.assembler.model.Module
import com.electrolytej.assembler.util.P
import com.electrolytej.assembler.util.ProjectUtil
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate

class ModuleAssemblerRootProjectPlugin : Plugin<Project>, ProjectEvaluationListener {
    override fun apply(p: Project) {
        val gradle = p.gradle
        val binaryModuleMap = LinkedHashMap<String, Module>()
        gradle.extra["binaryModuleMap"] = binaryModuleMap

        val allModules: List<Module>? by gradle.extra
        if (allModules.isNullOrEmpty()) {
            P.child("allModules empty")
            return
        }
        val activeBuildVariant: String by gradle.extra
        val groupId: String by gradle.extra
        val appModule: Module by gradle.extra
        val sourceModuleMap: LinkedHashMap<String, Module> by gradle.extra
        val excludeModuleMap: LinkedHashMap<String, Module> by gradle.extra

        allModules?.forEach { m ->
//            val versionName = p.extensions.getByType(VersionCatalogsExtension::class.java).named("libs").findVersion("versionName").get().requiredVersion
            m.binaryPath = "${groupId}:${m.simpleName}:${ProjectUtil.newVersion(p, m)}"
            if (!sourceModuleMap.containsKey(m.simpleName) && !excludeModuleMap.containsKey(m.simpleName)) {
                binaryModuleMap[m.simpleName] = m
            }
        }
        P.child("module info ========================================================================================")
        P.child("activeBuildVariant:$activeBuildVariant")
        P.child("module begin ========================================================================================")
        P.child("app    module\t${appModule.sourcePath}")
        sourceModuleMap.forEach { (simpleName, module) ->
            var name = ""
            if (module.format == "ndbundle") {
                name = "${module.sourcePath}(dynamic)"
            } else {
                name = module.sourcePath
            }
            P.child("source module\t$name")
        }
        excludeModuleMap.forEach { (_, module) ->
            var name = ""
            if (module.format == "ndbundle") {
                name = "${module.sourcePath}(dynamic)"
            } else {
                name = module.sourcePath
            }
            P.child("${P.read("exclude module")}\t$name")
        }
        binaryModuleMap.forEach { (simpleName, module) ->
            var name = ""
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

    override fun beforeEvaluate(project: Project) {
//        if (!project.subprojects.isEmpty()) return
    }

    override fun afterEvaluate(project: Project, state: ProjectState) {
        //if (!project.subprojects.isEmpty())return
        val activeBuildVariant: String by project.gradle.extra
        val sourcePath2SimpleNameMap: MutableMap<String, String> by project.gradle.extra
        val sourceModuleMap: LinkedHashMap<String, Module> by project.gradle.extra
        //all模式下不发布组件
        if (activeBuildVariant == "all") return
        val simpleName = sourcePath2SimpleNameMap[project.path]
        if (simpleName?.isNotEmpty() == true) {
            val m = sourceModuleMap[simpleName]
            if (m?.group == "fwk" || m?.format == "nsbundle" || m?.format == "ndbundle") {
//                project.plugins.apply("io.github.electrolytej.module-publisher-plugin")
//                project["publish"].with {
//                    name = simpleName
//                    groupId = project.gradle.groupId
//                    artifactId = simpleName
//                    version = newVersion(project, m)
//                    website = "https://github.com/big-frontend/module-assembler"
//                }
            }
        }
    }


}

