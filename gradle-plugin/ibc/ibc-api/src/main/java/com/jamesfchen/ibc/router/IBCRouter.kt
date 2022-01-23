package com.jamesfchen.ibc.router

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.jamesfchen.ibc.Constants
import com.jamesfchen.ibc.Registry
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
        fun open(cxt: Context,builder: UriBuilder) {
            if (builder.uri.startsWith("/")) {
                //todo
                builder.uri = "${builder.uri}"
            }
            val ibcUri = builder.build()
            val router = Registry.getInstance().findRouter(ibcUri.host)
            if (ibcUri.schema == "http" || ibcUri.schema == "https") {
                router.onOpen(cxt, ibcUri.uri)
            } else {
                router.onOpen(cxt, ibcUri.page, ibcUri.params)
            }
        }
        fun open(cxt: Context, block: UriBuilder.() -> Unit) {
            val builder = UriBuilder()
            block(builder)
            open(cxt,builder)
        }
    }


}