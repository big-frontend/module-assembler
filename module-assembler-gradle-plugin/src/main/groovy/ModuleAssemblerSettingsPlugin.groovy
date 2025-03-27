import com.electrolytej.assembler.model.Module
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ModuleAssemblerSettingsPlugin implements Plugin<Settings>{

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
        Iterator<Module> iterator = config.allModules.iterator()
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
        def excludeModuleMap = new LinkedHashMap<String, Module>()
        def sourceModuleMap = new LinkedHashMap<String, Module>()

        def sourcePath2SimpleNameMap = [:]
//        pluginSrcModuleMap = [:]
//        pluginBinaryModuleMap = [:]
        def findModule = { name ->
            for (Module m : config.allModules) {
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
            excludeModuleMap[it.simpleName] = (Module)it
        }
        sourceModulesStr.eachAfterSplit(',') {
            sourceModuleMap[it.simpleName] = (Module)it
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
//        def p =  new BuildPerf()
//        gradle.addBuildListener(p)
//        gradle.addProjectEvaluationListener(p)

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

}

