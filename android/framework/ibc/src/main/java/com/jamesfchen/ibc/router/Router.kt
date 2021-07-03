package com.jamesfchen.ibc.router

import java.net.URI

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 *
 */
class Router {
    companion object{
        fun goto(uriStr: String){
            goto(URI.create(uriStr))
        }
        fun goto(uri: URI){
            println("cjf ${uri}  ${uri.scheme} ${uri.host} ${uri.path}  ${uri.query}")
            if (uri.scheme =="http"){
                //如果http内部调整不了就发送到外部应用，比如浏览器就可以处理

            }else{
            }
        }
    }

}