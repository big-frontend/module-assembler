package com.jamesfchen.moduleify

import com.jamesfchen.moduleify.P
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin implements Plugin<Project> {
    static String routerName
    static String routerPlugin
    static String routerLibrary
    static boolean isFirst = true
    static String navigationVersion
    static String lifecycleVersion
    @Override
    void apply(Project project) {
        if (isFirst) {
            navigationVersion = project.rootProject.findProperty("NAVIGATION_VERSION")
            lifecycleVersion = project.rootProject.findProperty("LIFECYCLE_VERSION")
            (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            P.info("pick up router, $routerLibrary")
            isFirst = false
        }
        addPlugins(project)
        project.plugins.apply('kotlin-android')
        project.plugins.apply('kotlin-kapt')
        def simepleName = project.gradle.sourcePath2SimpleNameMap[project.path]
        if (simepleName) {
            project.plugins.apply("io.github.jamesfchen.module-publisher-plugin")
            project['publish'].with {
                name = simepleName
                groupId = project.gradle.groupId
                artifactId = simepleName
                version = "1.0.0-${project.gradle.activeBuildVariant}-SNAPSHOT"
                website = "https://github.com/JamesfChen/bundles-assembler"
            }
        }
        project.android {
            compileSdkVersion Integer.parseInt(project.rootProject.compileSdkVersion)
            buildToolsVersion project.rootProject.buildToolsVersion
            defaultConfig {
                (minSdkVersion, targetSdkVersion, versionCode, versionName) = [Integer.parseInt(project.rootProject.minSdkVersion),
                                                                               Integer.parseInt(project.rootProject.targetSdkVersion),
                                                                               project.rootProject.versionCode, project.rootProject.versionName]

                testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }
            kotlinOptions {
                jvmTarget = "1.8"
            }
            buildTypes {
                debug {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
            }
            variantFilter { variant ->
                def flavors = variant.flavors*.name
                def buildType = variant.buildType.name
                def a = project.gradle.activeBuildVariant.toLowerCase()
                if (!a.contains(buildType)) {
                    setIgnore(true)
                }
                for (def flavor in flavors) {
                    if (!a.contains(flavor)) {
                        setIgnore(true)
                    }
                }

            }
        }
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
//            findDeps = { someOne ->
//                def myPath
//                if (someOne instanceof Project) {
//                    myPath = someOne.path //source path
//                } else {
//                    myPath = someOne //binary path
//                }
//                def pickupModule = null
//                if (isSourcePath(myPath)) {
//                    project.gradle.sourceModuleMap.each { _, m ->
//                        if (m.sourcePath == myPath) {
//                            pickupModule = m
//                            return
//                        }
//                    }
//                } else {
//                    project.gradle.binaryModuleMap.each { _, m ->project
//                        if (m.binaryPath == myPath) {
//                            pickupModule = m
//                            return
//                        }
//                    }
//                }
//                d = []
//                if (pickupModule != null) {
//                    if (pickupModule.deps == null) return []
//                    pickupModule.deps.each { dep ->
//                        def path = findModulePath(dep)
//                        if (path != null) {
//                            if (isSourcePath(path)) {
//                                path = project.gradle.rootProject.findProject(path)
//                            }
//                            d.add(path)
//                        }
//                    }
//                }
//                return d
//
//            }
            findModule = { simpleName ->
                if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.binaryModuleMap[simpleName]
                } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
                    return project.gradle.ext.sourceModuleMap[simpleName]
                }
                return null
            }
//            importDepTree = { def myProject, Closure closure ->
//                HashSet<String> visited = new HashSet<String>()
//                def deps = myProject.findDeps(myProject)
//                recurImport(visited, deps, closure)
//            }
//            importDeps = { def myProject, Closure closure ->
//                HashSet<String> visited = new HashSet<String>()
//                def deps = myProject.depsConfig("",myProject)
//            }
            findArtifact = {
                def myGroupId = ''
                def myArtifactId = ''
                def myVersion = ''
                def moduleSimpleName = ''
                def sourcePath = ''
                def binaryPath = ''
                for (def module : gradle.ext.modules) {
                    if (project.path == module.sourcePath) {
                        moduleSimpleName = module.simpleName
                        sourcePath = module.sourcePath
                        binaryPath = module.binaryPath
                        myGroupId = binaryPath.split(":")[0]
                        myArtifactId = binaryPath.split(":")[1]
                        myVersion = binaryPath.split(":")[2]
                        break
                    }
                }
                if ((moduleSimpleName == null || moduleSimpleName.isEmpty())
                        || (sourcePath == null || sourcePath.isEmpty())
                        || (binaryPath == null || binaryPath.isEmpty())
                ) {
                    throw IllegalArgumentException("moduleSimpleName:${moduleSimpleName} :sourcePath${sourcePath} :binaryPath${binaryPath} 这些参数都要配置")
                }
                return [moduleSimpleName, myGroupId, myArtifactId, myVersion]
            }
        }
        onApply(project)
    }

    static boolean isSourcePath(def path) {
        return !path.contains(".")
    }

//    void recurImport(def visited, def deps, Closure closure) {
//        if (deps == null || deps.size() == 0) return
//        for (def d : deps) {
//            if (d instanceof String) {
//                if (!visited.add(d)) {
//                    continue
//                }
//            } else if (!visited.add(d.path)) {
//                continue
//            }
//            recurImport(visited, closure.call(d), closure)
//        }
//    }

    abstract void addPlugins(Project project)

    abstract void onApply(Project project)
    protected static def pickupRouter(Project project) {
        if (project.rootProject.findProperty("AROUTER_VERSION")
                && project.rootProject.findProperty("WROUTER_VERSION")
                && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("三个只能选择一个")
        } else if (project.rootProject.findProperty("IBCROUTER_VERSION") && project.rootProject.findProperty("WROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("AROUTER_VERSION") && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("WROUTER_VERSION") && project.rootProject.findProperty("AROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        }
        if (project.rootProject.findProperty("AROUTER_VERSION")) return ["ARouter", "com.alibaba.arouter", "com.alibaba:arouter-api:$project.rootProject.AROUTER_VERSION"]
        if (project.rootProject.findProperty("WROUTER_VERSION")) return ["WRouter", "WMRouter", "io.github.meituan-dianping:router:$project.rootProject.WROUTER_VERSION"]
        if (project.rootProject.findProperty("IBCROUTER_VERSION")) return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
        return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
    }
}
