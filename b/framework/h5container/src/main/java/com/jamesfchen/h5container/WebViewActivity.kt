package com.jamesfchen.h5container

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jamesfchen.h5container.databinding.ActivityWebviewBinding

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
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "url为空", Toast.LENGTH_LONG).show()
            return
        }
        binding.wvContainer.loadUrl(url)
        binding.wvContainer.webViewClient = H5WebViewClient()
        binding.wvContainer.settings.javaScriptEnabled = true
        binding.wvContainer.settings.allowFileAccess = true//可以使用file:// 访问
        binding.wvContainer.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.wvContainer.webChromeClient = H5WebChromeClient()
        WebView.setWebContentsDebuggingEnabled(true);
    }
}