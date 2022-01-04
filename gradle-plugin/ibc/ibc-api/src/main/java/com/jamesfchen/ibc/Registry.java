package com.jamesfchen.ibc;

import android.util.Log;

import androidx.collection.ArrayMap;

import com.jamesfchen.ibc.cbpc.IExport;
import com.jamesfchen.ibc.router.IModuleRouter;
import com.jamesfchen.ibc.router.ISchemaRouter;

import static com.jamesfchen.ibc.Constants.ROUTER_TAG;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/04/2022  Tue
 */
public class Registry {
    private static final String ROUTER_CONFIG = "BundleManifest.xml";
    private ArrayMap<String, ISchemaRouter> schemaRouters;
    private ArrayMap<String, IModuleRouter> moduleRouters;
    private ArrayMap<String, Class<?>> registerRouters;
    private ArrayMap<Class<?>, Class<?>> registerApis;
    private ArrayMap<Class<?>, IExport> apis;

    Registry(){
        moduleRouters = new ArrayMap<>();
        schemaRouters = new ArrayMap<>();
        registerRouters = new ArrayMap<>();
        this.registerApis = new ArrayMap<>();
        this.apis = new ArrayMap<>();
    }
    public static Registry getInstance() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        static final Registry INSTANCE = new Registry();

    }
    void getFlutterRouters() {
    }

    void getReactNativeRouters() {
    }

    void getNativeRouters() {
    }

    public void registerRouter(String routerName, Class<?> clz) {
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
    public void registerApi(Class<?> clz) {
        registerApis.put(clz.getSuperclass(), clz);
    }
    public <T> T findApi(Class<T> clz) {
        T api = (T) apis.get(clz);
        if (api ==null){
            Class<?> aClass = registerApis.get(clz);
            if (aClass ==null){
                throw new IllegalArgumentException(clz.getCanonicalName() +" api没有注册");
            }
            try {
                api = (T) aClass.newInstance();
                apis.put(clz, (IExport) api);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                Log.d(ROUTER_TAG,Log.getStackTraceString(e));
                return null;
            }
        }
        return api;
    }

}
