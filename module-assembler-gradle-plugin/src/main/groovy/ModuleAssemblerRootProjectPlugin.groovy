import com.electrolytej.b.P
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.artifacts.VersionCatalogsExtension

class ModuleAssemblerRootProjectPlugin implements Plugin<Project>,ProjectEvaluationListener {
    @Override
    void apply(Project p) {
        def gradle = p.gradle
        gradle.ext.binaryModuleMap = new LinkedHashMap<String, Object>()
        gradle.ext.allModules.each { m ->
            m.binaryPath = "${gradle.ext.groupId}:${m.simpleName}:${newVersion(p, m)}"
            if (!gradle.ext.sourceModuleMap.containsKey(m.simpleName) && !gradle.ext.excludeModuleMap.containsKey(m.simpleName)) {
                gradle.ext.binaryModuleMap[m.simpleName] = m
            }
        }
        child("module info ========================================================================================")
        child("activeBuildVariant:" + gradle.ext.activeBuildVariant)
        child("module begin ========================================================================================")
        child("app    module\t${gradle.ext.appModule.sourcePath}")
        gradle.ext.sourceModuleMap.each { simpleName, module ->
            def name =""
            if (module.format == "ndbundle"){
                name = "${module.sourcePath}(dynamic)"
            }else{
                name = module.sourcePath
            }
            child("source module\t$name")
        }
        gradle.ext.excludeModuleMap.each { _, module ->
            def name =""
            if (module.format == "ndbundle"){
                name = "${module.sourcePath}(dynamic)"
            }else{
                name = module.sourcePath
            }
            child("${P.read("exclude module")}\t$name")
        }
        gradle.ext.binaryModuleMap.each { simpleName, module ->
            def name =""
            if (module.format == "ndbundle"){
                name = "${module.binaryPath}(dynamic)"
            }else{
                name = module.binaryPath
            }
            child("${P.yellow("binary module")}\t$name")
        }

        child("module end ========================================================================================")
        gradle.addProjectEvaluationListener(this)
    }

    @Override
    void beforeEvaluate(Project project) {
//        if (!project.subprojects.isEmpty()) return
    }

    @Override
    void afterEvaluate(Project project, ProjectState state) {
        //if (!project.subprojects.isEmpty())return
        //all模式下不发布组件
        if (project.gradle.ext.activeBuildVariant == 'all') return
        def simpleName = project.gradle.sourcePath2SimpleNameMap[project.path]
        if (simpleName) {
            def m = project.gradle.sourceModuleMap[simpleName]
            if (m.group == 'fwk' || m.format == 'nsbundle' || m.format == 'ndbundle') {
                project.plugins.apply("io.github.electrolytej.module-publisher-plugin")
                project['publish'].with {
                    name = simpleName
                    groupId = project.gradle.groupId
                    artifactId = simpleName
                    version = newVersion(project, m)
                    website = "https://github.com/big-frontend/module-assembler"
                }
            }
        }
    }

    static def child(Object msg) {
        println("👶[ gradle initialzation ] " + msg);
    }
    static def newVersion(Project project, def m) {
        def gradle = project.gradle
        def versionName = project.extensions.getByType(VersionCatalogsExtension).named("libs").findVersion("versionName").get().requiredVersion
        def branch = gitBranch()
//        def ts = System.currentTimeMillis();
        def buildId = getCommitId()
        def ver = "${branch}-$versionName-${gradle.activeBuildVariant}-${buildId}-SNAPSHOT"
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
    static def releaseTime() {
        return new Date().format("yyMMddHHmm", TimeZone.getTimeZone("GMT+08:00"))
    }

    static def getUserName(){
        return 'git config user.name'.execute().text.trim()
    }

    static def getCommitId() {
        return 'git rev-parse --short HEAD'.execute().text.trim()
    }
    static def gitBranch() {
        def branch = ""
        def proc = "git rev-parse --abbrev-ref HEAD".execute()
        proc.in.eachLine { line -> branch = line }
        proc.err.eachLine { line -> println line }
        proc.waitFor()
        return branch
    }

}

