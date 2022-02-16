package com.jamesfchen.ibc.router

import android.content.Context
import android.os.Bundle
import java.net.URI

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * author: jamesfchen
 * email: hawksjamesf@gmail.com
 * since: 六月/30/2021  星期三
 *
 */
interface IRouter {
    /**
     * native bundle 、 react native bundle 、 flutter bundle
     */
    fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean = false

    /**
     * h5网页
     */
    fun onOpen(cxt: Context, uri: URI,params: Bundle?): Boolean = false
}