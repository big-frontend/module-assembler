package com.jamesfchen.ibc.router;

import static com.jamesfchen.ibc.Constants.ROUTER_TAG;

import android.util.Log;

import androidx.collection.ArrayMap;

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author jamesfchen
 * @email hawksjamesf@gmail.com
 * @since 7月/08/2021  周四
 */
public class RoutersManager {
    private ArrayMap<String, ISchemaRouter> schemaRouters;
    private ArrayMap<String, IModuleRouter> moduleRouters;
    private ArrayMap<String, Class<?>> registerRouters;
    private static final String ROUTER_CONFIG = "BundleManifest.xml";
    RoutersManager(){
        moduleRouters = new ArrayMap<>();
        schemaRouters = new ArrayMap<>();
        registerRouters = new ArrayMap<>();
    }
    public static RoutersManager getInstance() {
        return LazyHolder.INSTANCE;
    }


    void getFlutterRouters() {
    }

    void getReactNativeRouters() {
    }

    void getNativeRouters() {
    }

    public void register(String routerName, Class<?> clz) {
        registerRouters.put(routerName, clz);
    }

    public IModuleRouter findModuleRouter(String routerName) {
        IModuleRouter moduleRouter = moduleRouters.get(routerName);
        if (moduleRouter ==null){
            Class<?> aClass = registerRouters.get(routerName);
            if (aClass ==null){
                throw new IllegalArgumentException(routerName +" 路由器没有注册");
            }
            try {
                moduleRouter = (IModuleRouter) aClass.newInstance();
                moduleRouters.put(routerName, (IModuleRouter) moduleRouter);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                Log.d(ROUTER_TAG,Log.getStackTraceString(e));
                return null;
            }
        }
        return moduleRouter;
    }

    public ISchemaRouter findSchemaRouter(String routerName) {
        ISchemaRouter schemaRouter = schemaRouters.get(routerName);
        if (schemaRouter ==null){
            Class<?> aClass = registerRouters.get(routerName);
            if (aClass ==null){
                throw new IllegalArgumentException(routerName +" 路由器没有注册");
            }
            try {
                schemaRouter = (ISchemaRouter) aClass.newInstance();
                schemaRouters.put(routerName, (ISchemaRouter) schemaRouter);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                Log.d(ROUTER_TAG,Log.getStackTraceString(e));
                return null;
            }
        }
        return schemaRouter;
    }
    private static class LazyHolder {
        static final RoutersManager INSTANCE = new RoutersManager();

    }
}
