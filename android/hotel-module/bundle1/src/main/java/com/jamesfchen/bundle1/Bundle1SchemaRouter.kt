package com.jamesfchen.bundle1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.ISchemaRouter
import java.net.URI

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
@Router(name = "bundle1SchemaRouter")
class Bundle1SchemaRouter : ISchemaRouter {

    override fun onOpenUri(cxt: Context, uri: URI): Boolean {
        Log.d("cjf","bundle1SchemaRouter")
        return false
    }
}