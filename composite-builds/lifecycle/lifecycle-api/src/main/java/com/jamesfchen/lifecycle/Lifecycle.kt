package com.jamesfchen.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

interface IActivitiesLifecycleObserver : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onActivityForeground(activity)"))
    override fun onActivityStarted(activity: Activity) {
        onActivityForeground(activity)
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onActivityBackground(activity)"))
    override fun onActivityStopped(activity: Activity) {
        onActivityBackground(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    fun onActivityForeground(activity: Activity) {}
    fun onActivityBackground(activity: Activity) {}
    fun onActivityWindowFocusChanged(activity: Activity, hasFocus: Boolean) {}
}

interface IAppLifecycleObserver : DefaultLifecycleObserver, IActivitiesLifecycleObserver {
    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppCreate()"))
    override fun onCreate(owner: LifecycleOwner) {
        onAppCreate()
    }

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppForeground()"))
    override fun onStart(owner: LifecycleOwner) {
        onAppForeground()
    }

    @Deprecated("废弃该回调")
    override fun onResume(owner: LifecycleOwner) {
    }

    @Deprecated("废弃该回调")
    override fun onPause(owner: LifecycleOwner) {
    }

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppBackground()"))
    override fun onStop(owner: LifecycleOwner) {
        onAppBackground()
    }

    @Deprecated("App进程销毁不会调用该回调")
    override fun onDestroy(owner: LifecycleOwner) {
    }

    //App#attachBaseContext --> ContentProvider#onCreate--->App#onCreate
    //onAppCreate 主要在 ContentProvider#onCreate 发送事件，该事件属于stick，当有观察者被注册，立马就会将stick事件发送出去
    fun onAppCreate() {}
    fun onAppForeground() {}
    fun onAppBackground() {}
}

abstract class IAppLifecycleObserverAdapter : IAppLifecycleObserver, IActivitiesLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

    override fun onAppCreate() {
        super.onAppCreate()
    }

    override fun onAppForeground() {
        super.onAppForeground()
    }

    override fun onAppBackground() {
        super.onAppBackground()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivityForeground(activity: Activity) {
    }

    override fun onActivityBackground(activity: Activity) {
    }

    override fun onActivityWindowFocusChanged(activity: Activity, hasFocus: Boolean) {
    }
}

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class AppLifecycle


interface IActivityLifecycleObserver : DefaultLifecycleObserver, IActivitiesLifecycleObserver {
    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppCreate()"))
    override fun onCreate(owner: LifecycleOwner) {
        onActivityCreate()
    }

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppForeground()"))
    override fun onStart(owner: LifecycleOwner) {
        onActivityForeground()
    }

    @Deprecated("废弃该回调")
    override fun onResume(owner: LifecycleOwner) {
    }

    @Deprecated("废弃该回调")
    override fun onPause(owner: LifecycleOwner) {
    }

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onAppBackground()"))
    override fun onStop(owner: LifecycleOwner) {
        onActivityBackground()
    }

    @Deprecated("换了个脸面，改了个名字", ReplaceWith("onActivityDestroy()"))
    override fun onDestroy(owner: LifecycleOwner) {
        onActivityDestroy()
    }

    //App#attachBaseContext --> ContentProvider#onCreate--->App#onCreate
    //onAppCreate 主要在 ContentProvider#onCreate 发送事件，该事件属于stick，当有观察者被注册，立马就会将stick事件发送出去
    fun onActivityCreate() {}
    fun onActivityForeground() {}
    fun onActivityWindowFocusChanged(hasFocus: Boolean) {}
    fun onActivityBackground() {}
    fun onActivityDestroy() {}
}
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class ActivityLifecycle
