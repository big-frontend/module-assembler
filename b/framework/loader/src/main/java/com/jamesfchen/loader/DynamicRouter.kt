package com.jamesfchen.loader

import android.content.Context
import android.os.Bundle
import com.jamesfchen.ibc.router.IRouter
import java.net.URI

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Aug/28/2022  Sun
 *
 *
 * 封装 插件/h5 js bundle/rn js bundle的下载 、 安装 、 加载
 */
open class DynamicRouter : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        return super.onOpen(cxt, page, params)
    }

    override fun onOpen(cxt: Context, uri: URI, params: Bundle?): Boolean {
        return super.onOpen(cxt, uri, params)
    }
}