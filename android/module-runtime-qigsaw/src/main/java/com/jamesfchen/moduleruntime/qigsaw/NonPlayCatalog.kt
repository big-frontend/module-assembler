package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class NonPlayArtifact(
    val splitName: String,
    val url: String,
    val sha256: String,
    val sizeBytes: Long,
)

data class NonPlayModuleDefinition(
    val moduleName: String,
    val splitName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val minHostVersionCode: Long,
    val artifacts: List<NonPlayArtifact>,
)

class NonPlayCatalog(
    definitions: List<NonPlayModuleDefinition>,
) {
    private val definitionMap = definitions.associateBy { it.moduleName }
    private val definitionList = definitions.toList()

    fun get(moduleName: String): NonPlayModuleDefinition? = definitionMap[moduleName]

    fun contains(moduleName: String): Boolean = definitionMap.containsKey(moduleName)

    fun definitions(): List<NonPlayModuleDefinition> = definitionList
}

object AssetNonPlayCatalogLoader {
    fun load(
        context: Context,
        assetName: String = DEFAULT_ASSET_NAME,
    ): NonPlayCatalog {
        val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
        return parse(json)
    }

    fun loadOrEmpty(
        context: Context,
        assetName: String = DEFAULT_ASSET_NAME,
    ): NonPlayCatalog {
        return runCatching { load(context, assetName) }
            .getOrElse { NonPlayCatalog(emptyList()) }
    }

    fun parse(json: String): NonPlayCatalog {
        val root = JSONObject(json)
        val modules = root.optJSONArray("modules") ?: JSONArray()
        val definitions = buildList {
            for (index in 0 until modules.length()) {
                val item = modules.getJSONObject(index)
                add(
                    NonPlayModuleDefinition(
                        moduleName = item.getString("moduleName"),
                        splitName = item.optString("splitName", item.getString("moduleName")),
                        packageName = item.getString("packageName"),
                        versionName = item.optString("versionName", "0"),
                        versionCode = item.optLong("versionCode", 0L),
                        minHostVersionCode = item.optLong("minHostVersionCode", 0L),
                        artifacts = item.optJSONArray("artifacts").toArtifacts(),
                    )
                )
            }
        }
        return NonPlayCatalog(definitions)
    }

    private fun JSONArray?.toArtifacts(): List<NonPlayArtifact> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val item = getJSONObject(index)
                add(
                    NonPlayArtifact(
                        splitName = item.getString("splitName"),
                        url = item.getString("url"),
                        sha256 = item.optString("sha256", ""),
                        sizeBytes = item.optLong("sizeBytes", 0L),
                    )
                )
            }
        }
    }

    const val DEFAULT_ASSET_NAME: String = "dynamic-feature-catalog.json"
}
