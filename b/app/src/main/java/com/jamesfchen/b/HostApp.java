package com.jamesfchen.b;

import android.content.Context;
import android.content.res.Configuration;

import com.jamesfchen.loader.BApp;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Feb/12/2022  Sat
 */
class HostApp extends BApp {
    RePluginApplicationProxy rePluginApplicationProxy;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        rePluginApplicationProxy = new RePluginApplicationProxy();
        rePluginApplicationProxy.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rePluginApplicationProxy.onCreate();
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
