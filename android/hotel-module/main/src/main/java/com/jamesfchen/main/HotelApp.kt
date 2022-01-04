package com.jamesfchen.main

import com.jamesfchen.ibc.IBCInitializer
import com.jamesfchen.loader.BApp

@com.jamesfchen.lifecycle.App
class HotelApp : BApp() {
    override fun onCreate() {
        super.onCreate()
        IBCInitializer.init(applicationContext);

    }
}