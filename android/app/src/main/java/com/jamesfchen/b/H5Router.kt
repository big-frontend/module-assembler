package com.jamesfchen.b

import android.content.Context
import android.os.Bundle
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.IRouter
import java.net.URI

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/12/2022  Wed
 */
@Router(bindingBundle = "h5container")
class H5Router : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {

//        val url = params?.getString(EXTRA_URL)
//        if (!url.isNullOrEmpty()) {
//            WebViewActivity.startActivity(cxt, url)
//            return true
//        }
        return false
    }

    override fun onOpen(cxt: Context, uri: URI, params: Bundle?): Boolean {
        if (uri.toString().isNotEmpty()) {
//            WebViewActivity.startActivity(cxt, uri.toString())
            return true
        }
        return false

    }
}