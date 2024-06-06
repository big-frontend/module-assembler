

import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings

class ModuleRegistryPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        def gradle = settings.gradle
        def rootDir = settings.rootDir
        /**
         * 读取module_config.json信息以此来include具体的模块，对于模块的描述应该有这些信息
         * class Module{*    require  def simpleName 模块名的简写,给idea plugin读取
         *    require def format 模块格式(nsbundle ndbundle h5bundle rnbundle flutterbundle foundation jar ,bundle可以不参加编译，即exclude，但是framework foundation必须被include)
         *    require def group 分组是为了当exclude某个app时，其下的依赖的同组模块也会exclude
         *    require def type {api,processor,tool} 对于framework内部的模块来说，有暴露给app层的api模块，还有有processor、tool
         *    require def sourcePath :components:hotel-module:foundation
         *    require def binaryPath default: {package}:simpleName:1.0.0 ,默认的binary_artifact需要保证simpelName唯一性,先暂时用1.0.0站位，后面应该通过获取远程版本和本地版本进行自动升级
         *    option def deps 不应有这个属性，要编译成什么应该通过excludeModule和sourceModule,默认都是aar编译//option def build_source(source or binary),binary(aar jar)编译更快
         *}*/
        def config = new JsonSlurper().parse(new File("$rootDir/module_config.json"))
        gradle.ext.allModules = config.allModules
        gradle.ext.groupId = config.groupId
        gradle.ext.buildVariants = config.buildVariants
        def localProperties = new Properties()
        def localPropertiesFile = new File(rootDir, 'local.properties')
        if (localPropertiesFile.exists()) {
            localPropertiesFile.withReader('UTF-8') { reader -> localProperties.load(reader)}
        }
        def excludeModulesStr = localProperties.getProperty('excludeModules', '')
        def sourceModulesStr = localProperties.getProperty('sourceModules', '')
        if (localProperties.getProperty('activeBuildVariant', '')) {
            gradle.ext.activeBuildVariant = localProperties.getProperty('activeBuildVariant', '')
        } else {
            gradle.ext.activeBuildVariant = gradle.ext.buildVariants.get(0)
            localProperties.setProperty("activeBuildVariant", gradle.ext.activeBuildVariant)
            localProperties.store(new FileOutputStream(localPropertiesFile), "update modules")
        }
        gradle.ext.excludeModuleMap = new LinkedHashMap<String, Object>()
        gradle.ext.sourceModuleMap = new LinkedHashMap<String, Object>()
        gradle.ext.binaryModuleMap = new LinkedHashMap<String, Object>()
        gradle.ext.sourcePath2SimpleNameMap = [:]

        gradle.ext.pluginSrcModuleMap = [:]
        gradle.ext.pluginBinaryModuleMap = [:]
        def findModule = { name ->
            for (def m : gradle.ext.allModules) {
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
            gradle.ext.excludeModuleMap[it.simpleName] = it
        }
        sourceModulesStr.eachAfterSplit(',') {
            gradle.ext.sourceModuleMap[it.simpleName] = it
            gradle.ext.sourcePath2SimpleNameMap[it.sourcePath] = it.simpleName
            if (it.dynamic) {
                gradle.ext.pluginSrcModuleMap[it.simpleName] = it
            }
        }
        gradle.ext.allModules.each { m ->
            m.binaryPath = "${gradle.ext.groupId}:${m.simpleName}:${newVersion(gradle, m)}"
            if (!gradle.ext.sourceModuleMap.containsKey(m.simpleName) && !gradle.ext.excludeModuleMap.containsKey(m.simpleName)) {
                gradle.ext.binaryModuleMap[m.simpleName] = m
            }
        }
        settings.include ":app"
        child("module info ========================================================================================")
        child("activeBuildVariant:" + gradle.ext.activeBuildVariant)
        child("module begin ========================================================================================")
        gradle.ext.sourceModuleMap.each { name, module ->
            child("source module\t${module.sourcePath}")
            settings.include module.sourcePath
            if (module.projectDir) {
                project(module.sourcePath).projectDir = new File(rootProject.projectDir, module.projectDir)
            }
        }
        gradle.ext.excludeModuleMap.each { _, module -> child("\u001B[31mexclude module\u001B[0m\t${module.sourcePath}")}
        gradle.ext.binaryModuleMap.each { simpleName, module ->
            child("binary module\t${module.binaryPath}")
            if (module.dynamic) {
                gradle.ext.pluginBinaryModuleMap[simpleName] = module
            }
        }

        child("module end ========================================================================================")
//这些只能在settings.gradle使用,是属于初始化阶段的钩子
//gradle.settingsEvaluated {
//    println("settingsEvaluated")
//
//}
//gradle.projectsLoaded { g ->
//    println("projectsLoaded")
//}
        gradle.ext.modules = gradle.ext.sourceModuleMap.values() + gradle.ext.binaryModuleMap.values()


        gradle.addProjectEvaluationListener(new ProjectEvaluationListener() {
            def projStart = 0

            @Override
            void beforeEvaluate(Project project) {
//        if (!project.subprojects.isEmpty()) return
                projStart = System.currentTimeMillis()
            }

            @Override
            void afterEvaluate(Project project, ProjectState state) {
                //if (!project.subprojects.isEmpty())return
                //all模式下不发布组件
                if (gradle.ext.activeBuildVariant == 'all') return
                def simpleName = project.gradle.sourcePath2SimpleNameMap[project.path]
                if (simpleName) {
                    def m = project.gradle.sourceModuleMap[simpleName]
                    if (m.group == 'fwk' || m.format == 'nsbundle' || m.format == 'ndbundle'
//                    || m.format == 'h5bundle' || m.format == 'h5bundle' || m.format == 'rnbundle'
                    ) {
                        project.plugins.apply("io.github.jamesfchen.module-publisher-plugin")
                        project['publish'].with {
                            name = simpleName
                            groupId = project.gradle.groupId
                            artifactId = simpleName
                            version = newVersion(project.gradle, m)
                            website = "https://github.com/JamesfChen/bundles-assembler"
                        }
                    }
                    if (m.format == 'ndbundle') {
                        project.android.defaultConfig.versionCode = Integer.parseInt(m.versionCode)
                        project.android.defaultConfig.versionName = m.versionName
                        project.version = Integer.parseInt(m.versionCode)
                    }
                }
            }
        })

    }
    static def child(Object msg) {
        println("👶[ gradle initialzation ] " + msg);
    }
    static def newVersion(def gradle, def m) {
        def ver = "1.0.0-${gradle.activeBuildVariant}-SNAPSHOT"
        if (m.versionName) {
            if (!m.versionCode) throw new IllegalArgumentException("参数错误 versionName:${m.versionName} versionCode:${m.versionCode}")
            def a = m.versionName.split('-')
            if (a.size() == 1) {
                ver = "${a[0]}-${gradle.activeBuildVariant}"
            } else if (a.size() == 2) {
                ver = "${a[0]}-${gradle.activeBuildVariant}-SNAPSHOT"
            } else {
                throw new IllegalArgumentException("参数错误 versionName:${m.versionName} versionCode:${m.versionCode}")
            }
        }
        return ver
    }
}
