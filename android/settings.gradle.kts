pluginManagement {
    repositories {
        maven(uri("$rootDir/repo"))
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google/")
        maven("https://artifact.bytedance.com/repository/byteX/")
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    includeBuild("../module-assembler-gradle-plugin")
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(uri("$rootDir/repo"))
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://jitpack.io")
        maven("https://artifact.bytedance.com/repository/byteX/")
        mavenCentral()
        google()
    }
}

apply("./scripts/trace_gradle.gradle")

include(":dynamicfeature")
include(":dynamicfeature2")

plugins {
    id("io.github.jamesfchen.module-registry-plugin")
}

