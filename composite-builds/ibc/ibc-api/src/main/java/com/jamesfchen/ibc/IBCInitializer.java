package com.jamesfchen.ibc;

import android.content.Context;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/04/2022  Tue
 */
public class IBCInitializer {
    static boolean inited = false;

    public static void init(Context cxt) {
        if (inited) return;
        register(cxt);
        inited = true;
    }

    /**
     * 通过编译期插桩注册路由器
     */
    private static void register(Context cxt) {
        //        Registry.getInstance().registerRouter("home", HomeRouter.class);
//        Registry.getInstance().registerRouter("bundle1", Bundle1Router.class);
//        Registry.getInstance().registerRouter("bundle2", Bundle2Router.class);
//        Registry.getInstance().registerRouter("h5container", H5Router.class);
//        Registry.getInstance().registerApi(CallImp.class);
    }
}
