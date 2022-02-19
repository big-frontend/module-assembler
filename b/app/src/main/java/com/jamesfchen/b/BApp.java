package com.jamesfchen.b;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jamesfchen.ibc.IBCInitializer;
import com.jamesfchen.lifecycle.App;
import com.jamesfchen.loader.BuildConfig;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: 2017/7/4
 */
@App
public class BApp extends Application {
    private static BApp sApp;
    RePluginApplicationProxy rePluginApplicationProxy;
    public static BApp getInstance() {
        if (sApp == null) {
            throw new IllegalStateException("app is null");
        }
        return sApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        rePluginApplicationProxy = new RePluginApplicationProxy();
        rePluginApplicationProxy.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fresco.initialize(this);
        if (BuildConfig.DEBUG) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this);
//        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver());
        IBCInitializer.init(this);
        rePluginApplicationProxy.onCreate();
        Log.d("cjf","cjf");
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        rePluginApplicationProxy.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        rePluginApplicationProxy.onTrimMemory(level);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rePluginApplicationProxy.onConfigurationChanged(newConfig);
    }
}