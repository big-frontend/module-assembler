package com.jamesfchen.ibc

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 *
 */
fun routers(config:() -> Unit) {
    return config()
}
fun router(name:String,scheme:String,page: () -> Unit = {}){
    page()
}
fun page(name:String,param: () -> Unit){}
fun param(name:String,type:String){}

/* 下面的配置可以通过gradle transform api收集多个bundle的BundleManifest.xml merge而来*/
val routerConfig = routers {
    println("cjf routers")
    router(name = "com.jamesfchen.bundle1.Bundle1Router",scheme = "bundle"){
        page(name ="com.jamesfchen.bundle1.SayMeActivity"){
            param(
                name = "from",
                type = "String"
            )
        }
    }
    router(
        name = "com.jamesfchen.bundle1.HttpRouter", scheme = "http"
    )
    router(name = "com.jamesfchen.bundle2.Bundle2Router",scheme = "bundle"){
        page(name ="com.jamesfchen.bundle2.SayHiActivity"){
            param(
                name = "from",
                type = "String"
            )
        }
        page(name ="com.jamesfchen.bundle2.MainActivity"){
            param(
                name = "from",
                type = "String"
            )
        }
    }
}
