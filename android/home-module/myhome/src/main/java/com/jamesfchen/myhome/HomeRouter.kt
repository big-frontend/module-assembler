package com.jamesfchen.myhome

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
@Router(bindingBundle = "home")
class HomeRouter : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        if ("ppp".equals(page, ignoreCase = true)) {
            val intent = Intent(cxt, P::class.java)
            cxt.startActivity(intent)
            return true
        }
        return false
    }
}
