package com.jamesfchen.ibc.router;

import static com.jamesfchen.ibc.Constants.ROUTER_TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.net.URI;

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author jamesfchen
 * @email hawksjamesf@gmail.com
 * @since 7月/08/2021  周四
 */
public class IBCRouter {
    public static void init(Context cxt) {
        //通过编译期插桩注册路由器
//            RoutersManager.getInstance().register(Bundle1Router::class.java)
//            RoutersManager.getInstance().register(Bundle2Router::class.java)
//            RoutersManager.getInstance().register("bundle1",Class.forName(" com.jamesfchen.bundle1.Bundle1Router"))
//            RoutersManager.getInstance().register("bundle2",Class.forName(" com.jamesfchen.bundle2.Bundle1Router"))
    }

    /**
     *  通过模块名的方式打开页面
     * @param cxt
     * @param routerName
     * @param page
     * @param ext
     */
    public static void go(Context cxt,String routerName,String page, Bundle ext) {
        Log.d(ROUTER_TAG, routerName+" "+page+" "+ext);
        IModuleRouter router = RoutersManager.getInstance().findModuleRouter(routerName);
        router.onGo(cxt, page, ext);
    }
    public static void go(Context cxt,String routerName,String page) {
        go(cxt,routerName,page,null);
    }
    /**
     * 通过schema的方式打开页面
     * @param cxt
     * @param uri
     *  * *
     *  * * scheme://router-name/page?param1=value1&param2=value2 ...
     *  * *
     *  * * scheme:
     *  * * - http/hybrid
     *  * * - reactnative
     *  * * - flutter
     *  * * - native
     */
    public static void openUri(Context cxt, URI uri) {
        Log.d(ROUTER_TAG, uri+" "+uri.getScheme()+" "+uri.getHost()+" "+uri.getPath()+" "+uri.getQuery());
        if (uri.getScheme().equals("http")) {
            //如果http内部处理不了就发送到外部应用，比如浏览器就可以处理
        } else if (uri.getScheme().equals("native")) {
            IModuleRouter router = RoutersManager.getInstance().findModuleRouter(uri.getHost());
            String[] split = uri.getPath().split("/");
            router.onGo(cxt, split[1], null);
        }else{
            ISchemaRouter schemaRouter = RoutersManager.getInstance().findSchemaRouter(uri.getHost());
            schemaRouter.onOpenUri(cxt,uri);
        }
    }
}
