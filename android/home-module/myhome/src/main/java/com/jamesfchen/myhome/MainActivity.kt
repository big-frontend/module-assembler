package com.jamesfchen.myhome

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jamesfchen.ibc.router.Router

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)

        tv.text = "${getPackageName()} home main activity"
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.BLACK)
        setContentView(tv)
        Router.goto("jamesfchen://bundle1/sayme")
    }

}