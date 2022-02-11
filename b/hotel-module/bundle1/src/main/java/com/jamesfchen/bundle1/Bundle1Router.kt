package com.jamesfchen.bundle1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.IRouter

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
@Router(bindingBundle = "bundle1")
class Bundle1Router : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        if ("sayme".equals(page, ignoreCase = true)) {
            val intent = Intent(cxt, SayMeActivity::class.java)
            cxt.startActivity(intent)
            return true
        }
        return false
    }
}
