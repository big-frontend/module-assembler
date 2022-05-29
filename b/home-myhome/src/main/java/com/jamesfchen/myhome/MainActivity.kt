package com.jamesfchen.myhome

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.router.IBCRouter
import com.jamesfchen.myhome.databinding.ActivityMainBinding
import com.qihoo360.replugin.RePlugin

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btNative1.setOnClickListener {
//            打开当前bundle内部的页面
            IBCRouter.open(this) {
                uri = "/ppp"
            }
        }
        binding.btNative2.setOnClickListener {
            //打开当前bundle内部的页面
            IBCRouter.open(this) {
                uri = "b://bundle1/sayme"
            }
        }
        binding.btReact.setOnClickListener {
            //必须自定义router且bindingBundle=h5container
            IBCRouter.open(this) {
                uri = "https://spacecraft-plan.github.io/SpacecraftReact/#/"
                params(
                    "key2" to "cjf2",
                    "key3" to 1,
                    "key4" to true
                )
            }
        }
        binding.btH5.setOnClickListener {
            IBCRouter.open(this) {
                uri = "b://h5container/page"
                params(
                    "url" to "file:///android_asset/AApp.html",
                )
            }
        }
        binding.btRn.setOnClickListener {
            IBCRouter.open(this) {
                uri = "b://h5container/page"
                params(
                    "url" to "file:///android_asset/AApp.html",
                )
            }
        }
        binding.btIm.setOnClickListener {
            val i = Intent()
            i.component = ComponentName(this@MainActivity,"com.example.compose.jetchat.NavActivity")
            RePlugin.startActivity(this@MainActivity,i)
            IBCRouter.open(this) {
                uri = "b://im/nav"
            }
        }
        binding.btAndroidNavigator.setOnClickListener {
            val i = Intent(this@MainActivity,AndroidNavigatorActivity::class.java)
            startActivity(i)
        }
    }

}