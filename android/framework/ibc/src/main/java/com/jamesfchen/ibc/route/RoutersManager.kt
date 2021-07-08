package com.jamesfchen.ibc.route

import android.content.Context
import androidx.collection.ArrayMap

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
object RoutersManager {
    private var hybridRouters : ArrayMap<String, IHybridRouter>? = null
    private var flutterRouters :  ArrayMap<String, IFlutterRouter>? = null
    private var reactNativeRouters :  ArrayMap<String, IReactNativeRouter>? = null
    private var nativeRouters :  ArrayMap<String, INativeRouter>? = null
    private const val ROUTER_CONFIG="BundleManifest.xml"

    fun getHybridRouters(cxt:Context):ArrayMap<String, IHybridRouter>{
        if (hybridRouters ==null){
//            XmlParser.parse(cxt.assets.open(ROUTER_CONFIG))
        }
        return hybridRouters!!
    }
    fun getFlutterRouters(){}
    fun getReactNativeRouters(){}
    fun getNativeRouters(){}
}