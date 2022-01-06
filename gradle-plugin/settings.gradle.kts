//pluginManagement {
//    repositories {
//        maven {
//            url =uri("./local-repo")
//        }
//        gradlePluginPortal()
//    }
//    plugins {
//        id("io.github.jamesfchen.lifecycle-plugin")  version "1.0.0"
//        id("io.github.jamesfchen.ibc-plugin")  version "1.0.0"
//    }
//}
rootProject.name = "gradle-plugin"
include(
    ":base-plugin", "base-plugin-ktx",
    ":lifecycle:lifecycle-api", ":lifecycle:lifecycle-plugin",
    ":ibc:ibc-api", ":ibc:complier", "ibc:ibc-plugin", ":ibc:annotations",
)
include(":module-publisher-plugin")
