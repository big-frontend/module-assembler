package com.jamesfchen.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;
import com.jamesfchen.bundle1.HotelMainActivity;

public class StarterActivity extends Activity {
    @AddTrace(name = "StarterActivity_onCreate", enabled = true /* optional */)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("cjf","StarterActivity");
        startActivity(new Intent(this, HotelMainActivity.class));
    }
}