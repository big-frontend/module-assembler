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
include(
    ":base-plugin", ":base-plugin-ktx",
    ":lifecycle:lifecycle-api", ":lifecycle:lifecycle-plugin",
    ":ibc:ibc-api", ":ibc:compiler", "ibc:ibc-plugin", ":ibc:annotations",
)
include(":module-publisher-plugin")
include(":moduleify-plugin")
include(":moduleify-api")
