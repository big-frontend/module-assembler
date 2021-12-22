pluginManagement {
    repositories {
        maven {
            url =uri("./local-repo")
        }
        gradlePluginPortal()
    }
    plugins {
        id("com.jamesfchen.lifecycle-plugin")  version "1.0.0"
    }
}
rootProject.name = "gradle-plugin"
include(
    ":base-plugin",
    ":lifecycle:lifecycle-api", ":lifecycle:lifecycle-plugin",
    ":ibc:ibc-api", ":ibc:complier", "ibc:ibc-plugin", ":ibc:annotations",
)
