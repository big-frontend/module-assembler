package com.jamesfchen.h5container

import android.util.Log
import android.webkit.JavascriptInterface

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/01/2022  Sat
 */
class H5Plugin {
    val name: String
    get() = "pay"

    @JavascriptInterface
    fun say(): String {
        Log.d("cjf", " native say calling from js")
        return "sb"
    }
}