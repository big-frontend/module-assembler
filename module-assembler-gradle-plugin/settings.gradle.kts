dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../android/gradle/libs.versions.toml"))
        }
    }
}

include(":module-publisher-plugin")
include(":module-assembler-plugin")
