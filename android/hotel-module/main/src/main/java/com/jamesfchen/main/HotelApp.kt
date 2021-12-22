package com.jamesfchen.main

import com.jamesfchen.ibc.router.IBCRouter
import com.jamesfchen.loader.BApp

@com.jamesfchen.lifecycle.App
class HotelApp : BApp() {
    override fun onCreate() {
        super.onCreate()
        IBCRouter.init(applicationContext);

    }
}