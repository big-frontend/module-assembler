package com.jamesfchen.b

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.jamesfchen.myhome.MainActivity

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "splash activity"
        tv.setTextColor(Color.BLACK)
        setContentView(tv)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}