package com.jamesfchen.ibc.router

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable
import java.net.URI
import java.util.*

/**
 * * *
 * * * scheme://bundle/page?param1=value1&param2=value2 ...
 * * * e.g. 打开其他bundle的页面 b://hotel/sayhi or b://com.jamesfchen.rn/sayhi or b://com.jamesfchen.flutter/sayhi
 * * *      打开h5 http://jamesfchen.page.link(默认首页 http://jamesfchen.page.link/index.html)
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
    val page = uri.path.lowercase(Locale.getDefault()).replaceFirst("/", "")
    val params = builder.params
}

class UriBuilder {
    lateinit var uri: String
    var params: Bundle? = null
    fun params(vararg args: Pair<String, Any>) {
        params = Bundle()
        args.forEach {
            when (it.second) {
                is String -> params?.putString(it.first, it.second as String)
                is Array<*> -> params?.putStringArray(it.first, it.second as Array<String>)
                is Byte -> params?.putByte(it.first, it.second as Byte)
                is ByteArray -> params?.putByteArray(it.first, it.second as ByteArray)
                is Char -> params?.putChar(it.first, it.second as Char)
                is CharArray -> params?.putCharArray(it.first, it.second as CharArray)
                is Boolean -> params?.putBoolean(it.first, it.second as Boolean)
                is BooleanArray -> params?.putBooleanArray(it.first, it.second as BooleanArray)
                is Short -> params?.putShort(it.first, it.second as Short)
                is ShortArray -> params?.putShortArray(it.first, it.second as ShortArray)
                is Int -> params?.putInt(it.first, it.second as Int)
                is IntArray -> params?.putIntArray(it.first, it.second as IntArray)
                is Long -> params?.putLong(it.first, it.second as Long)
                is LongArray -> params?.putLongArray(it.first, it.second as LongArray)
                is Float -> params?.putFloat(it.first, it.second as Float)
                is FloatArray -> params?.putFloatArray(it.first, it.second as FloatArray)
                is Double -> params?.putDouble(it.first, it.second as Double)
                is DoubleArray -> params?.putDoubleArray(it.first, it.second as DoubleArray)
                is Parcelable -> params?.putParcelable(it.first, it.second as Parcelable)
                is Serializable -> params?.putSerializable(it.first, it.second as Serializable)
                is SparseArray<*> ->params?.putSparseParcelableArray(it.first, it.second as SparseArray<out Parcelable>)
                is Array<*> -> params?.putParcelableArray(it.first, it.second as Array<out Parcelable>)
                is IBinder -> params?.putBinder(it.first, it.second as IBinder)
            }
        }
    }

    fun build() = IBCUri(this)
}