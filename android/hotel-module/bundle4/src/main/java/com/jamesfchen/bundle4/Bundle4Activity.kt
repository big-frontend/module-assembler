package com.jamesfchen.bundle4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jamesfchen.moduleruntime.SplitActivityRuntime

class Bundle4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitActivityRuntime.install(this)
        val textView = TextView(this).apply {
            text = "bundle4 dynamic feature"
            textSize = 20f
        }
        setContentView(textView)
    }
}
