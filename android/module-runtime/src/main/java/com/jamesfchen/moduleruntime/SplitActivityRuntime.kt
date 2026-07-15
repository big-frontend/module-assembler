package com.jamesfchen.moduleruntime

import android.app.Activity
import java.util.concurrent.atomic.AtomicReference

fun interface SplitActivityInstaller {
    fun install(activity: Activity)
}

object SplitActivityRuntime {
    private val installerRef = AtomicReference(SplitActivityInstaller { })

    @JvmStatic
    fun register(installer: SplitActivityInstaller) {
        installerRef.set(installer)
    }

    @JvmStatic
    fun install(activity: Activity) {
        installerRef.get().install(activity)
    }
}
