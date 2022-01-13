package com.jamesfchen.bundle1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;
import com.jamesfchen.ibc.router.IBCRouter;

import java.net.URI;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jun/13/2021  Sun
 */
//@Route(path = "/bundle1/sayme")
public class SayMeActivity extends Activity {
    @AddTrace(name = "SayMeActivity_onCreate", enabled = true /* optional */)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button tv = new Button(this);
        tv.setText(getPackageName()+" bundle1 say me ");
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        tv.setAllCaps(false);
        setContentView(tv,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Simple jump within application (Jump via URL in 'Advanced usage')
//                ARouter.getInstance().build("/bundle2/main").navigation();
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("jamesfchen://www.jamesfchen.com/hotel/main"));
//                startActivity(intent);
                // 2. Jump with parameters
//                    ARouter.getInstance().build("/login/1")
//                            .withLong("key1", 666L)
//                            .withString("key3", "888")
//                            .navigation();
//                IBCRouter.go(SayMeActivity.this,"bundle2router","sayhi");
//                IBCRouter.openUri(SayMeActivity.this, URI.create("reactnative://bundle1SchemaRouter/bundle2"));
                IBCRouter.openUri(SayMeActivity.this, URI.create("webapp://webcontainerrouter/bundle3?url=file:///android_asset/AApp.html"));
//
            }
        });
    }

}
