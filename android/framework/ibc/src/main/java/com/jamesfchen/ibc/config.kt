package com.jamesfchen.ibc

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
interface Page {
    var name: String
}

interface RouterConfig {
    var pages: List<Page>
}


//fun config(config: RouterConfig.() -> Unit): Map<String, String> {
//}

//val c = config {
//    pages = mutableListOf()
//}

/**
 * "hotel-bundle1":[
 *      "com.jamesfchen.bundle1.Bundle1Router":{
 *            "com.jamesfchen.bundle1.SayMeActivity",
 *
 *      }
 *  "hotel-bundle1":[
 *       "com.jamesfchen.bundle2.Bundle2Router":{
 *              "com.jamesfchen.bundle2.MainActivity",
 *               "com.jamesfchen.bundle2.SayHiActivity",
 *
 *       }
 *  ]
 * ]
 */
val routerConfig: Map<String, String> = mapOf(
    "hotel-bundle1" to "com.jamesfche.bundle1.Bundle1Router",
    "hotel-bundle2" to "com.jamesfche.bundle1.Bundle2Router"
)