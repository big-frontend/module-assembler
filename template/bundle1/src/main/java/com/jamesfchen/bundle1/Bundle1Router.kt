package com.jamesfchen.bundle1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.IModuleRouter

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
@Router(name = "bundle1router")
class Bundle1Router : IModuleRouter {
    override fun onGo(cxt: Context, page: String, bundle: Bundle?): Boolean {
        if ("sayme".equals(page, ignoreCase = true)) {
            val intent = Intent(cxt, SayMeActivity::class.java)
            cxt.startActivity(intent)
            return true
        }
        return false
    }
}
