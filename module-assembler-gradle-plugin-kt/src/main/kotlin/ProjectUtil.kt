import com.electrolytej.assembler.model.Module
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun isSourcePath(path: String): Boolean {
    return !path.contains(".")
}

fun Project.depsConfig(configuration: String, simpleName: String) {
    try {
        val m = moduleify(simpleName)
        project.dependencies.add(configuration, m)
    } catch (_: Exception) {
    }
}

fun Project.moduleify(simpleName: String): Any {
    val path = findModulePath(simpleName)
    if (path.isNullOrEmpty()) return UnknownProjectException("不支持${simpleName}")
    if (isSourcePath(path)) {
        return project.rootProject.project(path)
    }
    return path
}

fun Project.findModulePath(simpleName: String): String? {
    val binaryModuleMap: LinkedHashMap<String, Module> by gradle.extra
    val sourceModuleMap: LinkedHashMap<String, Module> by gradle.extra
    if (binaryModuleMap.containsKey(simpleName)) {
        val module = binaryModuleMap[simpleName]
        if (module?.binaryPath.isNullOrEmpty()) {
            throw IllegalArgumentException("binary module 的binaryPath不能为空")
        }
        return module?.binaryPath //'com.jamesfchen:box-tool:1.0.0'
    } else if (sourceModuleMap.containsKey(simpleName)) {
        val module = sourceModuleMap[simpleName]
        if (module?.sourcePath.isNullOrEmpty()) {
            throw IllegalArgumentException("source module 的sourcePath不能为空")
        }
        if (module?.format == "ndbundle") {//动态库不能被app 模块引用
            return ""
        }
        return module?.sourcePath //':framework:common'
    }
//    //该模块为exclude，不会进行编译不需要依赖
    return null
}

//fun Project.findModule(simpleName: String) {
//    if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.binaryModuleMap[simpleName]
//    } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.sourceModuleMap[simpleName]
//    }
//    return null
//}

fun newVersion(project: Project, m: Module): String {
    val branch = project.gitBranch()
    val versionName = project.version
    //        def ts = System.currentTimeMillis();
    val buildId = project.getCommitId()
    val activeBuildVariant: String by project.gradle.extra
    var ver = "${branch}-$versionName-${activeBuildVariant}-${buildId}-SNAPSHOT"
    if (m.versionName.isNotEmpty()) {
        require(m.versionCode.isNotEmpty()) { "参数错误 versionName:${m.versionName} versionCode:${m.versionCode}" }
        val a = m.versionName.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        ver = if (a.size == 1) {
            "${a[0]}-${activeBuildVariant}"
        } else if (a.size == 2) {
            "${a[0]}-${activeBuildVariant}-SNAPSHOT"
        } else {
            throw java.lang.IllegalArgumentException("参数错误 versionName:${m.versionName} versionCode:${m.versionCode}")
        }
    }
    return ver
}

fun String.runCommand(workingDir: File): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        return proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}
//fun releaseTime(): String {
//    return Date().format("yyMMddHHmm", TimeZone.getTimeZone("GMT+08:00"))
//}

fun Project.getUserName(): String? {
//        Runtime.getRuntime().exec()
    return "git config user.name".runCommand(rootDir)?.trim()
}

fun Project.getCommitId(): String? {
    return "git rev-parse --short HEAD".runCommand(rootDir)?.trim()
}

fun Project.gitBranch(): String? {
    return "git rev-parse --abbrev-ref HEAD".runCommand(rootDir)?.trim()
}
