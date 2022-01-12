package com.jamesfchen.webcontainer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.jamesfchen.webcontainer.databinding.ActivityWebviewBinding

/**
 * Copyright ® $ 2020
 * All right reserved.
 *
 * @author: hawksjamesf
 * @email: hawksjamesf@gmail.com
 * @since: Aug/24/2020  Mon
 */
class WebViewActivity : Activity() {
    companion object {
        @JvmStatic
        fun startActivity(context: Context,uri:String) {
            val intent = Intent(context,WebViewActivity::class.java)
            intent.putExtra("url", uri)
            context.startActivity(intent)
        }
    }

    @JavascriptInterface
    fun nativeSay(): String {
        Log.d("cjf", " native say calling from js")
        return "sb"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra("url")?.let { url ->
            binding.wv.loadUrl("file:///android_asset/AApp.html")
            binding.wv.loadUrl(url)
            binding.wv.webViewClient = MyWebViewClient()
            binding.wv.settings.javaScriptEnabled = true
            binding.wv.settings.javaScriptCanOpenWindowsAutomatically = true
            binding.wv.webChromeClient = MyWebChromeClient()
            binding.wv.addJavascriptInterface(this@WebViewActivity, "myActivity")
//            binding.title.setOnClickListener {
//                binding.wv.evaluateJavascript("javascript:jsAlert()") {
//                    Log.d("cjf", "jsAlert ${it}")
//                }
//            }
        }
//        binding.title.setImageBitmap()
//        val wv = findViewById<WebView>(R.id.wv)
//        val h5Plugin = H5Plugin()
//        wv.addJavascriptInterface(h5Plugin,h5Plugin.name)
//        wv.evaluateJavascript()
//        file:///android_asset/test.html
    }
}