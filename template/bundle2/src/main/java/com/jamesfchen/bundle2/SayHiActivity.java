package com.jamesfchen.bundle2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jamesfchen.ibc.router.IBCRouter;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jun/19/2021  Sat
 */
//@Route(path = "/bundle2/sayhi")
public  class SayHiActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button tv = new Button(this);
        tv.setText("say hi bundle2");
        tv.setTextColor(Color.BLACK);
        tv.setAllCaps(false);
        setContentView(tv,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Simple jump within application (Jump via URL in 'Advanced usage')
//                ARouter.getInstance().build("/bundle1/sayme").navigation();
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("jamesfchen://www.jamesfchen.com/hotel/main"));
//                startActivity(intent);
                // 2. Jump with parameters
//                    ARouter.getInstance().build("/login/1")
//                            .withLong("key1", 666L)
//                            .withString("key3", "888")
//                            .navigation();
                    IBCRouter.go(SayHiActivity.this,"bundle1router","sayme");
            }
        });
    }

}