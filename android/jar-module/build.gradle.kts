import groovy.lang.Closure
plugins {
    id("java-library")
    kotlin("jvm")
//    id("io.github.electrolytej.module-publisher-plugin")
}
//publish {
//    name = "jar-module"
//    groupId = "io.github.jamesfchen"
//    artifactId = "jar-module"
//    version = "1.0.0-${gradle.activeBuildVariant}-SNAPSHOT"
//    website = "https://github.com/big-frontend/module-assembler"
//}


dependencies {
    val moduleify: Closure<Any> by project.extra
//    compileOnly(moduleify("fwk-base"))
    compileOnly("com.android.tools.lint:lint-api:30.3.0")
    compileOnly("com.android.tools.lint:lint-checks:30.3.0")
//    val  KOTLIN_VERSION :String by project
//    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${KOTLIN_VERSION}")
//    compileOnly("org.jetbrains.kotlin:kotlin-compiler:${KOTLIN_VERSION}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}