package com.electrolytej.assembler.model

import kotlinx.serialization.Serializable

/**
 * simpleName : app
 * canonicalName : :app
 * format : app
 * group : father
 * binary_artifact : com.jamesfchen.b:app:1.0
 * deps : [":hotel-module:bundle1",":hotel-module:bundle2"]
 */
@Serializable
data class Module(
    var group: String,
    var simpleName: String,
) {
    var sourcePath: String = ""
    var binaryPath: String = ""
    var projectDir: String = ""
    var format: String = ""
    var versionName: String = ""
    var versionCode: String = ""
    var dynamic: String? = null
    var type: Type = Type.NONE

    //    public String binary_artifact;


    enum class Type {
        NONE,

        //    AAR,
        //    JAR,
        BINARY,
        SOURCE,
        EXCLUDE
    }
}