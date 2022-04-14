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
        try {
            Class.forName(Constants.PKG +".generated.RegistryProxy")
                    .getMethod("register")
                    .invoke(null);
        //        register(cxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inited = true;
    }
}
