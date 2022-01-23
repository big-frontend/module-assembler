package com.jamesfchen.myhome

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jamesfchen.ibc.router.IBCRouter

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = Button(this)

        tv.text = "${getPackageName()}  main activity"
        tv.isAllCaps = false
        tv.setTextColor(Color.BLACK)
        tv.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("b://www.jamesfchen.com/hotel/bundle1")
            )
            startActivity(intent)
        }
        setContentView(tv, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))
    }

}