package com.jamesfchen.ibc.cbpc

import com.jamesfchen.ibc.Registry

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/04/2022  Tue
 */
object IBCCbpc {
    @JvmStatic
    fun <T> findApi(clz: Class<T>): T?  {
        return Registry.findApi(clz)
    }
}