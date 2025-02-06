import com.electrolytej.assembler.model.Module
import com.electrolytej.assembler.model.ModuleConfig
import com.electrolytej.assembler.util.P
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.Properties
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import org.gradle.kotlin.dsl.extra
import java.io.FileOutputStream

class ModuleAssemblerSettingsPlugin : Plugin<Settings>, ProjectEvaluationListener, BuildListener,
    TaskExecutionGraphListener, TaskExecutionListener {

    override fun apply(settings: Settings) {
        val gradle = settings.gradle
        val rootDir = settings.rootDir
        val defaultJson = Json {
            ignoreUnknownKeys = true
        }
        val config: ModuleConfig =
            defaultJson.decodeFromStream<ModuleConfig>(File("${rootDir}/module_config.json").inputStream())
        gradle.extra["allModules"] = config.allModules
        gradle.extra["groupId"] = config.groupId
        gradle.extra["buildVariants"] = config.buildVariants
        val iterator = config.allModules.iterator()
        val dynamicModules = arrayListOf<String>()
        var appModule: Module? = null
        while (iterator.hasNext()) {
            val module = iterator.next()
            if (module.format == "ndbundle") {
                dynamicModules.add(module.sourcePath)
            }
            if (module.simpleName == "app" || module.group == "host") {
                appModule = module
                iterator.remove()
            }
        }

        val localProperties = Properties()
        val localPropertiesFile = File(rootDir, "local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.reader().use { reader ->
                localProperties.load(reader)
            }
        }
        val excludeModulesStr = localProperties.getProperty("excludeModules")
        val sourceModulesStr = localProperties.getProperty("sourceModules")
        var activeBuildVariant = localProperties.getProperty("activeBuildVariant")
        if (activeBuildVariant.isNullOrBlank()) {
            activeBuildVariant = config.buildVariants[0]
            localProperties.setProperty("activeBuildVariant", activeBuildVariant)
            localProperties.store(FileOutputStream(localPropertiesFile), "update modules")
        }
        val excludeModuleMap = LinkedHashMap<String, Module>()
        val sourceModuleMap = LinkedHashMap<String, Module>()

        val sourcePath2SimpleNameMap = mutableMapOf<String, String>()
//        pluginSrcModuleMap = [:]
//        pluginBinaryModuleMap = [:]

        excludeModulesStr.eachAfterSplit(",") { name ->
            val m = config.findModule(name)
            if (m != null) {
                excludeModuleMap[m.simpleName] = m
            }

        }
        sourceModulesStr.eachAfterSplit(",") { name ->
            val m = config.findModule(name)
            if (m != null) {
                excludeModuleMap[m.simpleName] = m
                sourceModuleMap[m.simpleName] = m
                sourcePath2SimpleNameMap[m.sourcePath] = m.simpleName
//            if (it.dynamic) {
//                pluginSrcModuleMap[it.simpleName] = it
//            }
            }
        }
        appModule?.let {
            settings.include(it.sourcePath)
            if (it.projectDir.isNotEmpty()) {
                settings.project(appModule.sourcePath).projectDir =
                    File(settings.rootProject.projectDir, it.projectDir)
            }
        }

        sourceModuleMap.forEach { (name, module) ->
            settings.include(module.sourcePath)
            if (module.projectDir.isNotEmpty()) {
                settings.project(module.sourcePath).projectDir =
                    File(settings.rootProject.projectDir, module.projectDir)
            }
        }
        val dynamicModuleIterator = dynamicModules.iterator()
        while (dynamicModuleIterator.hasNext()) {
            val next = dynamicModuleIterator.next()
            var hasExit = false
            sourceModuleMap.forEach { (name, module) ->
                if (module.sourcePath == next) {
                    hasExit = true
                }
            }
            if (!hasExit) dynamicModuleIterator.remove()
        }

        gradle.addBuildListener(this)
        gradle.addProjectEvaluationListener(this)
        gradle.taskGraph.addTaskExecutionGraphListener(this)
        gradle.taskGraph.addTaskExecutionListener(this)
        gradle.extra["appModule"] = appModule
        gradle.extra["dynamicModules"] = dynamicModules
        gradle.extra["excludeModuleMap"] = excludeModuleMap
        gradle.extra["sourceModuleMap"] = sourceModuleMap
        gradle.extra["activeBuildVariant"] = activeBuildVariant
        gradle.extra["sourcePath2SimpleNameMap"] = sourcePath2SimpleNameMap
    }
//gradle.taskGraph.whenReady { taskGraph ->
//    println("👰🤵[ configuration ] whenReady task关系图建立完毕 start")
//    println("👰🤵[ configuration ] whenReady task关系图建立完毕 end")
//}
    var start = System.currentTimeMillis()

    override fun beforeSettings(settings: Settings) {
        P.error(">>>> beforeSettings")
    }
    /**
     *  这些只能在settings.gradle使用,是属于初始化阶段的钩子
     */
    override fun settingsEvaluated(settings: Settings) {
        P.error(">>>> evaluate完setting脚本耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    /**
     * project初始化完成的回调
     */
    override fun projectsLoaded(gradle: Gradle) {
        P.error(">>>> include完所有project 耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    override fun projectsEvaluated(gradle: Gradle) {
        P.error(">>>> evaluate完所有project脚本 耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    override fun buildFinished(buildResult: BuildResult) {
        P.error(">>>> gradle 结束 buildFinished")
    }


    var projStart = 0L
    override fun beforeEvaluate(project: Project) {
//        if (!project.subprojects.isEmpty()) return
        projStart = System.currentTimeMillis()
    }

    override fun afterEvaluate(project: Project, state: ProjectState) {
//        if (!project.subprojects.isEmpty()) return
        P.error(">>>> evaluate完${project.displayName}项目 耗时:" + (System.currentTimeMillis() - projStart) + "ms")
    }

    override fun graphPopulated(graph: TaskExecutionGraph) {

    }

    override fun beforeExecute(task: Task) {
//        P.error(">>>> evaluate完${task.name}任务 beforeExecute")
    }

    override fun afterExecute(task: Task, state: TaskState) {
//        P.error(">>>> evaluate完${task.name}任务 afterExecute")
    }

}


