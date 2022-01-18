package com.jamesfchen.webcontainer

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface

class JsiImpl(val activity: Activity) {
    @JavascriptInterface
    fun  getHostActivity() = activity
    @JavascriptInterface
    fun nativeSay(): String {
        Log.d("cjf", " native say calling from js")
        return "sb"
    }

}