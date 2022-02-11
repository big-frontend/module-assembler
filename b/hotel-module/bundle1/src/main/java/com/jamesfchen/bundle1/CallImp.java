package com.jamesfchen.bundle1;

import android.util.Log;

import com.jamesfchen.export.ICall;
import com.jamesfchen.ibc.Api;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/04/2022  Tue
 */
@Api
public class CallImp extends ICall{

    @Override
    public boolean call() {
        Log.d("cjf","onCall");
        return true;
    }
}