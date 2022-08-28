package com.jamesfchen.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.Router
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
class IMRouter : DynamicRouter() {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        if ("nav".equals(page, ignoreCase = true)) {
            val i = Intent()
            i.component = ComponentName(cxt, "com.example.compose.jetchat.NavActivity")
            RePlugin.startActivity(cxt, i)
            val pluginInstalled = RePlugin.isPluginInstalled("plugin-im")
            if (!pluginInstalled){
                //网络下载
            }
            return true
        }
        return false
    }
}