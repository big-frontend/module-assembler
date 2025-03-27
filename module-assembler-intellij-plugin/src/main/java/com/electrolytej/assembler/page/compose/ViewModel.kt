package com.electrolytej.assembler.page.compose

import org.jetbrains.annotations.ApiStatus
import java.util.*
import com.electrolytej.assembler.model.Module
@ApiStatus.Internal
data class ViewModel(
    @JvmField
    var activeBuildVariant: String = "",
    @JvmField
    var buildVariants: List<String>? = null,
    @JvmField
    val excludeModuleMap: Map<String, Module> = TreeMap(),
    var defaultGroupName1: String = "exclude",
    @JvmField
    val sourceModuleMap: Map<String, Module> = TreeMap(),
    var defaultGroupName2: String = "source",
    @JvmField
    val binaryModuleMap: Map<String, Module> = TreeMap(),
    var defaultGroupName3: String = "binary",
)