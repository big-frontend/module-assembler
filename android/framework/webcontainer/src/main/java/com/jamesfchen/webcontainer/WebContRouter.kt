package com.jamesfchen.webcontainer

import android.content.Context
import android.util.Log
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
        val query = uri.rawQuery
        WebViewActivity.startActivity(cxt,uri.query)
        return false
    }
}