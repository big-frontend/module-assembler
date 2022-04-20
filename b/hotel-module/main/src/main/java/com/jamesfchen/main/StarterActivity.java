package com.jamesfchen.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.jamesfchen.export.ICall;
import com.jamesfchen.ibc.cbpc.IBCCbpc;

public class StarterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("cjf", "StarterActivity");
        ICall api = IBCCbpc.findApi(ICall.class);
        if (api.call()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("jamesfchen://www.jamesfchen.com/hotel/bundle1"));
            startActivity(intent);
        }
//        startActivity(new Intent(this, Bundle1Activity.class));
    }
}