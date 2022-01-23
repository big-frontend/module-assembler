package com.jamesfchen.ibc.router

import android.os.Bundle
import java.net.URI
import java.util.*

/**
 * * *
 * * * scheme://bundle/page?param1=value1&param2=value2 ...
 * * * e.g. 打开其他bundle的页面 b://hotel/sayhi or b://com.jamesfchen.rn/sayhi or b://com.jamesfchen.flutter/sayhi
 * * *      打开h5 http://jamesfchen.page.link
 * * *      打开当前bundle内部的page /sayhi
 * * *      打开其他应用的页面(deep link/app link) meituan://
 * * *
 * * *
 *
 */
class IBCUri(private val builder: UriBuilder) {
    val uri: URI by lazy {
        URI.create(builder.uri)
    }
    val schema = uri.scheme
    val host = uri.host
    val page = uri.path.lowercase(Locale.getDefault()).replaceFirst("/","")
    val params = builder.params
}

class UriBuilder {
    lateinit var uri: String
    var params: Bundle? = null
    fun params(vararg args: Pair<String, Any>) {
        params = Bundle()
        args.forEach {
            when (it.second) {
                is String -> {
                    params?.putString(it.first, it.second as String)
                }
                is Boolean -> {
                    params?.putBoolean(it.first, it.second as Boolean)
                }
                is Short -> {
                    params?.putShort(it.first, it.second as Short)
                }
                is Int -> {
                    params?.putInt(it.first, it.second as Int)
                }
                is Long -> {
                    params?.putLong(it.first, it.second as Long)
                }
                is Float -> {
                    params?.putFloat(it.first, it.second as Float)
                }
                is Double -> {
                    params?.putDouble(it.first, it.second as Double)
                }
                is ShortArray -> {
                    params?.putShortArray(it.first, it.second as ShortArray)
                }
                is IntArray -> {
                    params?.putIntArray(it.first, it.second as IntArray)
                }
                //                is ArrayList<Int> -> {
//                    params.putIntegerArrayList(it.first, it.second as ArrayList<Int>)
//                }
                is LongArray -> {
                    params?.putLongArray(it.first, it.second as LongArray)
                }
                is FloatArray -> {
                    params?.putFloatArray(it.first, it.second as FloatArray)
                }
                is DoubleArray -> {
                    params?.putDoubleArray(it.first, it.second as DoubleArray)
                }
            }
        }
    }

    fun build() = IBCUri(this)
}