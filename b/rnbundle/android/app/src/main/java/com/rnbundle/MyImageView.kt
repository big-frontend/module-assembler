package com.rnbundle

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.events.RCTEventEmitter
import kotlin.math.log

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Mar/13/2022  Sun
 */
internal class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1
) : androidx.appcompat.widget.AppCompatImageView(
    context, attrs, defStyleAttr
) {
    init {
        Log.d("cjf","MyImageView init")
    }
    fun setSource(sources: ReadableArray?) {
        Log.d("cjf","source:"+sources)
    }
    fun setBorderRadius(borderRadius: Float) {
        Log.d("cjf","setBorderRadius:"+borderRadius)

    }
    fun setScaleType(toScaleType: ScalingUtils.ScaleType?) {}
    fun onReceiveNativeEvent() {
        val event = Arguments.createMap()
        event.putString("message", "MyMessage")
        val reactContext = context as ReactContext
        reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(id, "topChange", event)
    }
}