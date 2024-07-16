import groovy.json.JsonSlurper
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import com.electrolytej.b.P

class ModuleAssemblerSettingsPlugin implements Plugin<Settings>, ProjectEvaluationListener, BuildListener {

    @Override
    void apply(Settings settings) {
        def gradle = settings.gradle
        def rootDir = settings.rootDir
        /**
         * 读取module_config.json信息以此来include具体的模块，对于模块的描述应该有这些信息
         * class Module{
         *     require  def simpleName 模块名的简写,给idea plugin读取
         *    require def format 模块格式(nsbundle ndbundle h5bundle rnbundle flutterbundle foundation jar ,bundle可以不参加编译，即exclude，但是framework foundation必须被include)
         *    require def group 分组是为了当exclude某个app时，其下的依赖的同组模块也会exclude
         *    require def type {api,processor,tool} 对于framework内部的模块来说，有暴露给app层的api模块，还有有processor、tool
         *    require def sourcePath :components:hotel-module:foundation
         *    require def binaryPath default: {package}:simpleName:1.0.0 ,默认的binary_artifact需要保证simpelName唯一性,先暂时用1.0.0站位，后面应该通过获取远程版本和本地版本进行自动升级
         *    option def deps 不应有这个属性，要编译成什么应该通过excludeModule和sourceModule,默认都是aar编译//option def build_source(source or binary),binary(aar jar)编译更快
         *}*/
        def config = new JsonSlurper().parse(new File("$rootDir/module_config.json"))
        Iterator<Object> iterator = config.allModules.iterator()
        def dynamicModules = []
        def appModule = null
        while (iterator.hasNext()) {
            def module = iterator.next()
            if (module.format == "ndbundle") {
                dynamicModules.add(module.sourcePath)
            }
            if (module.simpleName == "app" || module.group == "host") {
                appModule = module
                iterator.remove()
            }
        }

        def localProperties = new Properties()
        def localPropertiesFile = new File(rootDir, 'local.properties')
        if (localPropertiesFile.exists()) {
            localPropertiesFile.withReader('UTF-8') { reader -> localProperties.load(reader) }
        }
        def excludeModulesStr = localProperties.getProperty('excludeModules', '')
        def sourceModulesStr = localProperties.getProperty('sourceModules', '')
        def activeBuildVariant = ""
        if (localProperties.getProperty('activeBuildVariant', '')) {
            activeBuildVariant = localProperties.getProperty('activeBuildVariant', '')
        } else {
            activeBuildVariant = config.buildVariants.get(0)
            localProperties.setProperty("activeBuildVariant", activeBuildVariant)
            localProperties.store(new FileOutputStream(localPropertiesFile), "update modules")
        }
        def excludeModuleMap = new LinkedHashMap<String, Object>()
        def sourceModuleMap = new LinkedHashMap<String, Object>()

        def sourcePath2SimpleNameMap = [:]
//        pluginSrcModuleMap = [:]
//        pluginBinaryModuleMap = [:]
        def findModule = { name ->
            for (def m : config.allModules) {
                if (m.simpleName == name) {
                    return m
                }
            }
            return null
        }
        String.metaClass.eachAfterSplit { reg, Closure closure ->
            delegate.split(reg).each { name ->
                def m = findModule(name)
                if (m != null) {
                    closure.call(m)
                }
            }
        }
        excludeModulesStr.eachAfterSplit(',') {
            excludeModuleMap[it.simpleName] = it
        }
        sourceModulesStr.eachAfterSplit(',') {
            sourceModuleMap[it.simpleName] = it
            sourcePath2SimpleNameMap[it.sourcePath] = it.simpleName
//            if (it.dynamic) {
//                pluginSrcModuleMap[it.simpleName] = it
//            }
        }

        settings.include appModule.sourcePath
        if (appModule.projectDir) {
            project(appModule.sourcePath).projectDir = new File(rootProject.projectDir, appModule.projectDir)
        }
        sourceModuleMap.each { name, module ->
            settings.include module.sourcePath
            if (module.projectDir) {
                project(module.sourcePath).projectDir = new File(rootProject.projectDir, module.projectDir)
            }
        }
        Iterator<Object> dynamicModuleIterator = dynamicModules.iterator();
        while (dynamicModuleIterator.hasNext()) {
            def next = dynamicModuleIterator.next()
            def hasExit = false
            sourceModuleMap.each { name, module ->
                if (module.sourcePath == next) {
                    hasExit = true
                }
            }
            if (!hasExit) dynamicModuleIterator.remove()
        }

        gradle.addBuildListener(this)
        gradle.addProjectEvaluationListener(this)


        gradle.ext.appModule = appModule
        gradle.ext.allModules = config.allModules
        gradle.ext.groupId = config.groupId
        gradle.ext.buildVariants = config.buildVariants
        gradle.ext.dynamicModules = dynamicModules
        gradle.ext.excludeModuleMap = excludeModuleMap
        gradle.ext.sourceModuleMap = sourceModuleMap
        gradle.ext.activeBuildVariant = activeBuildVariant
        gradle.ext.sourcePath2SimpleNameMap = sourcePath2SimpleNameMap
    }

//这些只能在settings.gradle使用,是属于初始化阶段的钩子
//gradle.settingsEvaluated {
//    println("settingsEvaluated")
//
//}
//gradle.projectsLoaded { g ->
//    println("projectsLoaded")
//}
//        gradle.ext.modules = gradle.ext.sourceModuleMap.values() + gradle.ext.binaryModuleMap.values()
//gradle.beforeSettings {
//    println("👶[ gradle 开始 ] buildStarted 开始之前 start")
//    println("👶[ gradle 开始 ] buildStarted 开始之前 end")
//}
//gradle.settingsEvaluated { g ->
//    println("👩‍🎓👨‍🎓[ initialzation ] settingsEvaluated setting.gradle脚本初始化完成 start")
//    println("👩‍🎓👨‍🎓[ initialzation ] settingsEvaluated setting.gradle脚本初始化完成 end")
//}
////project初始化完成的回调
//gradle.projectsLoaded {
//    println("👩‍🎓👨‍🎓[ initialzation ] projectsLoaded project初始化完成 start")
//    println("👩‍🎓👨‍🎓[ initialzation ] projectsLoaded project初始化完成 end")
//}
//
//project.afterEvaluate {}
//gradle.beforeProject {
//    println("👰🤵[ configuration ] beforeProject 某个build.gradle执行之前 start")
//    println("👰🤵[ configuration ] beforeProject 某个build.gradle执行之前 end")
//}
//project.afterEvaluate {}
//gradle.afterProject {
//    println("👰🤵[ configuration ] afterProject 某个build.gradle执行之后 start")
//    println("👰🤵[ configuration ] afterProject 某个build.gradle执行之后 end")
//}
//gradle.projectsEvaluated {
//    println("👰🤵[ configuration ] projectsEvaluated 所有build.gradle执行完毕 start")
//    println("👰🤵[ configuration ] projectsEvaluated 所有build.gradle执行完毕 end")
//}
//gradle.taskGraph.whenReady { taskGraph ->
//    println("👰🤵[ configuration ] whenReady task关系图建立完毕 start")
//    println("👰🤵[ configuration ] whenReady task关系图建立完毕 end")
//}
//
//gradle.taskGraph.beforeTask {theTask->
//    println("🏃👩‍💼👨‍💻[ run ${theTask.name}] beforeTask task关系图执行之前 start")
//    println("🏃👩‍💼👨‍💻[ run ${theTask.name}] beforeTask task关系图执行之前 end")
//}
//gradle.taskGraph.afterTask { theTask->
//    println("🏃👩‍💼👨‍💻[ run ${theTask.name}] afterTask task关系图执行之后 start")
//    println("🏃👩‍💼👨‍💻[ run ${theTask.name}] afterTask task关系图执行之后 end")
//}
//gradle.buildFinished {
//    println("👵👴[ gradle 结束 ] buildFinished  start")
//    println("👵👴[ gradle 结束 ] buildFinished  end")
//}
    def start = System.currentTimeMillis()

    @Override
    void beforeSettings(Settings settings) {

    }

    @Override
    void settingsEvaluated(Settings settings) {
        P.error(">>>> evaluate setting脚本耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        P.error(">>>> include完所有project 耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        P.error(">>>> evaluate完所有project脚本 耗时:" + (System.currentTimeMillis() - start) + "ms")
        start = System.currentTimeMillis()
    }

    @Override
    void buildFinished(BuildResult buildResult) {
        P.error(">>>> gradle 结束 buildFinished")
    }


    def projStart = 0

    @Override
    void beforeEvaluate(Project project) {
//        if (!project.subprojects.isEmpty()) return
        projStart = System.currentTimeMillis()
    }

    @Override
    void afterEvaluate(Project project, ProjectState state) {
//        if (!project.subprojects.isEmpty()) return
        P.error(">>>>evaluate ${project.getDisplayName()}项目 耗时:" + (System.currentTimeMillis() - projStart) + "ms")
    }
}

