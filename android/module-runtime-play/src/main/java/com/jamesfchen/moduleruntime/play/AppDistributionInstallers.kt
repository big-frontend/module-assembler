package com.jamesfchen.b

import android.app.Application
import com.jamesfchen.moduleruntime.ModuleInstaller
import com.jamesfchen.moduleruntime.play.PlayFeatureRuntime

object AppDistributionInstallers {
    @JvmStatic
    fun create(application: Application): List<ModuleInstaller> {
        return listOf(PlayFeatureRuntime.createInstaller(application))
    }
}
