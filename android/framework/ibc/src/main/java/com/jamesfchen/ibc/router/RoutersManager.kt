package com.jamesfchen.ibc.router

import kotlin.math.sign

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
object RoutersManager {
    private var bundleRouterMap: Map<String, IRouter>? = null
    private const val router_config="BundleManifest.xml"

    fun getBundleRouter(name: String): IRouter? {
        //parse xml
        if (bundleRouterMap == null) {

        }
        return bundleRouterMap?.get(name)

    }
}