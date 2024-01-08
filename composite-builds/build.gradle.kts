// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenLocal()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/public") }
        maven { url = uri("https://maven.aliyun.com/repository/public/") } //central+jcenter
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
//        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        val AGP_VERSION :String by project
        val KOTLIN_VERSION :String by project
        classpath("com.android.tools.build:gradle:${AGP_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${KOTLIN_VERSION}")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("io.github.jamesfchen:module-publisher-plugin:1.4.3")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
allprojects {
    repositories {
        mavenLocal()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/public") }
        maven { url = uri("https://maven.aliyun.com/repository/public/") } //central+jcenter
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
//        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_11.majorVersion
        targetCompatibility = JavaVersion.VERSION_11.majorVersion
    }
    tasks.withType<Test>().configureEach {
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.majorVersion
            apiVersion = "1.5"
            freeCompilerArgs = listOf("-Xno-optimized-callable-references")
        }
    }
}

tasks.register("clean", Delete::class.java) {
    description = "Remove all the build files and intermediate build outputs"
//    delete(allprojects.map { it.buildDir })
    delete(rootProject.buildDir)
}