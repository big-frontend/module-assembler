package com.jamesfchen.lifecycle;

import android.app.Application;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Mar/02/2022  Wed
 */
public class LifecycleInitializer {
    static boolean inited = false;
    static Application sApplication;

    public static void init(Application application) {
        if (inited) return;
        register(application);
        inited = true;
    }
    /**
     * 通过编译期插桩注册路由器
     */
    private static void register(Application application) {
        sApplication = application;
            //        registerInternal(application,StartupItem.class);
//                   registerInternal(application,AppLifecycleObserver.class);
    }

    /**
     *Class<?> aClass = Class.forName("com.jamesfchen.spacecraftplugin.AppLifecycleObserver");
     */
    public static void registerInternal(Application application,Class<?> clz){
        try {
            Object observer = (LifecycleObserver) clz.newInstance();
            ProcessLifecycleOwner.get().getLifecycle().addObserver((DefaultLifecycleObserver) observer);
            application.registerActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks) observer);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
