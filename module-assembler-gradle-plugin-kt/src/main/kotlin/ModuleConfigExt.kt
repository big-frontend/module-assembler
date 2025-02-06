import com.electrolytej.assembler.model.ModuleConfig
import com.electrolytej.assembler.model.Module

fun ModuleConfig.findModule(name: String?): Module? {
    for (m in this.allModules) {
        if (m.simpleName == name) {
            return m
        }
    }
    return null
}

fun String?.eachAfterSplit(reg: String, cb: (String) -> Unit) {
    if (this.isNullOrEmpty()) return
    split(reg).forEach { name ->
        cb.invoke(name)
    }
}