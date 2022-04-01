package com.rnbundle;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Mar/13/2022  Sun
 */
//@ReactModule(name = "CalendarModule")
public class CalendarModule extends ReactContextBaseJavaModule {
    CalendarModule(ReactApplicationContext context) {
        super(context);
    }
    @Override
    public String getName() {
        return "CalendarModule";
    }
    @ReactMethod(isBlockingSynchronousMethod = true)
    public String createCalendarEvent(String name, String location) {
        Log.d("cjf","createCalendarEvent");
        return "native createCalendarEvent";
    }
}