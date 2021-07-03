package com.jamesfchen.ibc.router

import android.util.SparseArray
import androidx.collection.ArrayMap
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jul/02/2021  Fri
 */
object XmlParser {
    fun parse(ist:InputStream,nodeName:String): ArrayMap<String, String> {
        val arrayMap = ArrayMap<String, String>()
        val factory = DocumentBuilderFactory.newInstance()
        val dct = factory.newDocumentBuilder()
            .parse(ist)
        val dctRoot = dct.documentElement
//        val element = dctRoot.getElementsByTagName(nodeName).item(0)
//        val elementsByTagName = dctRoot.getElementsByTagName(nodeName)

        return arrayMap
    }
}