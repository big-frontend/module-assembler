package com.jamesfchen.webcontainer

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.ISchemaRouter
import java.net.URI

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/12/2022  Wed
 */
@Router(name = "webcontainerrouter")
class WebContRouter :ISchemaRouter {

    override fun onOpenUri(cxt: Context, uri: URI): Boolean {
        val split = uri.rawQuery.split("=")
        WebViewActivity.startActivity(cxt,split[1])
        return false
    }
    @JavascriptInterface
    fun openUri(cxt: Context,uri:String): Boolean {
        WebViewActivity.startActivity(cxt,uri)
        return false
    }
}