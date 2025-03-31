import com.electrolytej.assembler.ModuleParser
import com.electrolytej.assembler.model.ModuleConfig
import com.electrolytej.assembler.util.P
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
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
import java.io.IOException

class ModuleAssemblerSettingsPlugin : Plugin<Settings>, ProjectEvaluationListener, BuildListener,
    TaskExecutionGraphListener, TaskExecutionListener {

    override fun apply(settings: Settings) {
        val gradle = settings.gradle
        val rootDir = settings.rootDir
        val config: ModuleConfig? = try {
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<ModuleConfig> = moshi.adapter(ModuleConfig::class.java)
            jsonAdapter.fromJson(File("${rootDir}/module_config.json").readText())
//        JsonReader(FileReader(this)).use { reader ->
//            return Gson().fromJson<ModuleConfig>(
//                reader,
//                ModuleConfig::class.java
//            )
//        }
        } catch (e: Exception) {
            null
        }
        if (config == null) return

        val localProperties = Properties()
        val localPropertiesFile = File(rootDir, "local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.reader().use { reader ->
                localProperties.load(reader)
            }
        }
        val parser = ModuleParser()
        try {
            parser.parser(localPropertiesFile, config)
        } catch (e: IOException) {
            return;
        }

        parser.appModule?.let {
            settings.include(it.sourcePath)
            if (it.projectDir?.isNotEmpty() == true) {
                settings.project(parser.appModule.sourcePath).projectDir =
                    File(settings.rootProject.projectDir, it.projectDir)
            }
        }

        parser.sourceModuleMap.forEach { (name, module) ->
            settings.include(module.sourcePath)
            if (module.projectDir?.isNotEmpty() == true) {
                settings.project(module.sourcePath).projectDir =
                    File(settings.rootProject.projectDir, module.projectDir)
            }
        }
        gradle.addBuildListener(this)
        gradle.addProjectEvaluationListener(this)
        gradle.taskGraph.addTaskExecutionGraphListener(this)
        gradle.taskGraph.addTaskExecutionListener(this)
        gradle.extra["allModules"] = config.allModules
        gradle.extra["groupId"] = config.groupId
        gradle.extra["buildVariants"] = config.buildVariants
        gradle.extra["appModule"] = parser.appModule
        gradle.extra["dynamicModules"] = parser.dynamicModules
        gradle.extra["excludeModuleMap"] = parser.excludeModuleMap
        gradle.extra["sourceModuleMap"] = parser.sourceModuleMap
        gradle.extra["activeBuildVariant"] = parser.activeBuildVariant
        gradle.extra["sourcePath2SimpleNameMap"] = parser.sourcePath2SimpleNameMap
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


