package com.jamesfchen.h5container

import android.net.http.SslError
import android.util.Log
import android.webkit.*

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jan/13/2022  Thu
 */
class H5WebViewClient : WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        Log.d("cjf","shouldInterceptRequest1:${request?.url}")
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        Log.d("cjf","shouldInterceptRequest2 ${url}")
        return super.shouldInterceptRequest(view, url)
    }
    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        //handler.cancel(); 默认的处理方式，WebView变成空白页
        handler.proceed()
        //handleMessage(Message msg); 其他处理
    }
}