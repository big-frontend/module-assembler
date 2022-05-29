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
    ":ibc:ibc-api", ":ibc:ibc-processor", ":ibc:ibc-gradle-plugin",
    ":temple-gradle-plugin"
)
include(":module-publisher-plugin")
include(":bundles-assembler-plugin")
include(":replugin-host-api")
include(":ndbundle-api")
