package com.jamesfchen.ibc.router

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.jamesfchen.ibc.Constants
import com.jamesfchen.ibc.Registry
import java.lang.UnsupportedOperationException
import java.net.URI

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author jamesfchen
 * @email hawksjamesf@gmail.com
 * @since 7月/08/2021  周四
 */
class IBCRouter {
    companion object {

        @JvmStatic
        fun open(cxt: Context, builder: UriBuilder) {
            if (builder.uri.startsWith("/")) {
                //todo：查询当前bundle名并且进行拼接
                builder.uri = "b:// /${builder.uri}"
                throw UnsupportedOperationException("暂时不支持跳转")
            }
            val ibcUri = builder.build()
            if (ibcUri.schema == "http"
                || ibcUri.schema == "https"
            ) {
                val router = Registry.findRouter("h5container")
                router?.onOpen(cxt, ibcUri.uri, ibcUri.params)
            } else {
                val router = Registry.findRouter(ibcUri.host)
                router?.onOpen(cxt, ibcUri.page, ibcUri.params)
            }
        }

        fun open(cxt: Context, block: UriBuilder.() -> Unit) {
            val builder = UriBuilder()
            block(builder)
            open(cxt, builder)
        }
    }


}