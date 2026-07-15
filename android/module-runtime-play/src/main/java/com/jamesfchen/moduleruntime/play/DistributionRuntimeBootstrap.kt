package com.jamesfchen.b

import android.app.Application
import android.content.res.Resources
import com.jamesfchen.moduleruntime.SplitActivityRuntime
import com.jamesfchen.moduleruntime.play.PlayFeatureRuntime

object DistributionRuntimeBootstrap {
    @JvmStatic
    fun attachBaseContext(application: Application) {
        PlayFeatureRuntime.install(application)
        SplitActivityRuntime.register(PlayFeatureRuntime::installActivity)
    }

    @JvmStatic
    fun onCreate(application: Application) = Unit

    @JvmStatic
    fun getResources(resources: Resources): Resources = resources
}
