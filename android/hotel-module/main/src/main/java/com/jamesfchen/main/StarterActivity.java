package com.jamesfchen.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;

public class StarterActivity extends Activity {
    @AddTrace(name = "StarterActivity_onCreate", enabled = true /* optional */)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("cjf", "StarterActivity");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("jamesfchen://www.jamesfchen.com/hotel/bundle1"));
        startActivity(intent);
//        startActivity(new Intent(this, Bundle1Activity.class));
    }
}