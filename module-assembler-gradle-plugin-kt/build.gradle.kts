import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.21"

    alias(libs.plugins.kotlinxSerialization)
}

group ="io.github.electrolytej"
version = "2.0.0"
// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
sourceSets {
    main {
        java.srcDirs("../module-assembler-common")
    }
}

dependencies {
//    compileOnly(libs.android.gradlePlugin)
//    compileOnly(libs.android.tools.common)
//    compileOnly(libs.compose.gradlePlugin)
//    compileOnly(libs.firebase.crashlytics.gradlePlugin)
//    compileOnly(libs.firebase.performance.gradlePlugin)
//    compileOnly(libs.kotlin.gradlePlugin)
//    compileOnly(libs.ksp.gradlePlugin)
//    compileOnly(libs.room.gradlePlugin)
//    implementation(libs.truth)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("moduleAssemblerRootProjectPlugin") {
            id = "${group}.module-assembler-rootproject-plugin"
            implementationClass = "ModuleAssemblerRootProjectPlugin"
        }
        register("moduleAssemblerSettingsPlugin") {
            id = "${group}.module-assembler-settings-plugin"
            implementationClass = "ModuleAssemblerSettingsPlugin"
        }
//        register("sbundlePlugin") {
//            id = "${group}.static-bundle-plugin"
//            implementationClass = "StaticBundlePlugin"
//        }
//        register("dbundlePlugin") {
//            id = "${group}.dynamic-bundle-plugin"
//            implementationClass = "DynamicBundlePlugin"
//        }
//        register("foundationPlugin") {
//            id = "${group}.foundation-plugin"
//            implementationClass = "FoundationModulePlugin"
//        }
//        register("apiPlugin") {
//            id = "${group}.api-plugin"
//            implementationClass = "ApiModulePlugin"
//        }
        register("appPlugin") {
            id = "${group}.app-plugin"
            implementationClass = "AppModulePlugin"
        }
    }
}
