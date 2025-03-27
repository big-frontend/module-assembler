import com.electrolytej.assembler.util.ProjectUtil
import org.gradle.api.Project

fun Project.depsConfig(configuration: String, simpleName: String) {
    ProjectUtil.depsConfig(this, configuration, simpleName)
}

fun Project.moduleify(simpleName: String): Any {
    return ProjectUtil.moduleify(this, simpleName)
}

fun Project.findModulePath(simpleName: String): String? {
    return ProjectUtil.findModulePath(this, simpleName)
}

//fun Project.findModule(simpleName: String) {
//    if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.binaryModuleMap[simpleName]
//    } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.sourceModuleMap[simpleName]
//    }
//    return null
//}