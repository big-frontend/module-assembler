package com.jamesfchen.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "splash activity"
        tv.setTextColor(Color.BLACK)
        setContentView(tv)
        Thread.sleep(2000)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}