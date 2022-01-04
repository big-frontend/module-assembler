package com.jamesfchen.main
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.Router
import com.jamesfchen.ibc.router.IModuleRouter

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/01/2022  Sat
 */
@Router(name = "toprouter")
class TopRouter : IModuleRouter {
    override fun onGo(cxt: Context, page: String, bundle: Bundle?): Boolean {
        return false
    }
}