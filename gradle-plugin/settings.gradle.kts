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
    ":ibc:ibc-api", ":ibc:ibc-compiler", ":ibc:ibc-plugin", ":ibc:ibc-annotations",
)
include(":module-publisher-plugin")
include(":moduleify-plugin")
include(":pg-annotations")
include(":pluginify:pluginify-plugin")
include(":pluginify:pluginify-host-api")
include(":pluginify:pluginify-parasite-api")
