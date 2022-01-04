package com.jamesfchen.ibc.router

import android.content.Context
import java.net.URI

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * author: jamesfchen
 * since: Jul/02/2021  Fri
 */
interface ISchemaRouter {
    fun onOpenUri(cxt: Context, uri: URI): Boolean
}