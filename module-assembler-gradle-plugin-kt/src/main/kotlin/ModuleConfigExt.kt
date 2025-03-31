import com.electrolytej.assembler.model.Module
import com.electrolytej.assembler.model.ModuleConfig
import com.electrolytej.assembler.util.ProjectUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.File

fun File.fromJson(): ModuleConfig? {
    try {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ModuleConfig> = moshi.adapter(ModuleConfig::class.java)
        return jsonAdapter.fromJson(this.readText())
//        JsonReader(FileReader(this)).use { reader ->
//            return Gson().fromJson<ModuleConfig>(
//                reader,
//                ModuleConfig::class.java
//            )
//        }
    } catch (e: Exception) {
        return null
    }
    return null
}