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
        //通过编译期插桩注册路由器
//            Registry.getInstance().register(Bundle1Router::class.java)
//            Registry.getInstance().register(Bundle2Router::class.java)
//            Registry.getInstance().register("bundle1",Class.forName(" com.jamesfchen.bundle1.Bundle1Router"))
//            Registry.getInstance().register("bundle2",Class.forName(" com.jamesfchen.bundle2.Bundle1Router"))
        inited = true;
    }
}
