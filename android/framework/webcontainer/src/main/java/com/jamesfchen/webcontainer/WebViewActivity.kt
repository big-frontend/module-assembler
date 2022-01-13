package com.jamesfchen.webcontainer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.jamesfchen.ibc.IBCInitializer
import com.jamesfchen.ibc.Registry
import com.jamesfchen.ibc.router.IBCRouter
import com.jamesfchen.webcontainer.databinding.ActivityWebviewBinding

/**
 * Copyright ® $ 2020
 * All right reserved.
 *
 * @author: hawksjamesf
 * @email: hawksjamesf@gmail.com
 * @since: Aug/24/2020  Mon
 */
class WebViewActivity : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun startActivity(context: Context,uri:String) {
            val intent = Intent(context,WebViewActivity::class.java)
            intent.putExtra("url", uri)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra("url")?.let { url ->
            binding.wvContainer.loadUrl(url)
            binding.wvContainer.webViewClient = MyWebViewClient()
            binding.wvContainer.settings.javaScriptEnabled = true
            binding.wvContainer.settings.allowFileAccess = true//可以使用file:// 访问
            binding.wvContainer.settings.javaScriptCanOpenWindowsAutomatically = true
            binding.wvContainer.webChromeClient = MyWebChromeClient()
            binding.wvContainer.addJavascriptInterface(JsiImpl(this), "pisces")
            val findSchemaRouter = Registry.getInstance().findSchemaRouter("webcontainerrouter")
            if (findSchemaRouter !=null){
                binding.wvContainer.addJavascriptInterface(findSchemaRouter, "ibcrouter")
            }
//            binding.title.setOnClickListener {
//                binding.wvContainer.evaluateJavascript("javascript:jsAlert()") {
//                    Log.d("cjf", "jsAlert ${it}")
//                }
//            }
        }
    }
}