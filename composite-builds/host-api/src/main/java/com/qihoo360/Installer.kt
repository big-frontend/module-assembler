package com.qihoo360

import com.qihoo360.replugin.RePlugin

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Aug/28/2022  Sun
 *
 *
 * 封装 插件/h5 js bundle/rn js bundle的下载 、 安装 、 加载
 */
object Installer{
    fun install(pluginName:String):Boolean{
        val pluginInstalled = RePlugin.isPluginInstalled(pluginName)
        if (!pluginInstalled){
            //网络下载
        }
        return true
    }
}