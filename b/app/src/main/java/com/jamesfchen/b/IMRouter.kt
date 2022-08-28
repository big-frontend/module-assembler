package com.jamesfchen.b

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.IRouter
import com.qihoo360.Installer
import com.qihoo360.replugin.RePlugin

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: May/29/2022  Sun
 *
 * im plugin模块
 */
@Router(bindingBundle = "im")
class IMRouter : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        val success = Installer.install("plugin-im")
        if (!success) return false
        if ("nav".equals(page, ignoreCase = true)) {
            val i = Intent()
            i.component = ComponentName(cxt, "com.example.compose.jetchat.NavActivity")
            RePlugin.startActivity(cxt, i)
        }
        return false
    }
}