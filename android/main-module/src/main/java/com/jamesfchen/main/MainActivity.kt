package com.jamesfchen.main

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.jamesfchen.main.databinding.ActivityMainBinding
import com.jamesfchen.moduleruntime.ModuleOpenRequest
import com.jamesfchen.moduleruntime.ModuleOpenResult
import com.jamesfchen.moduleruntime.ModuleRuntimeRegistry

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btIm.setOnClickListener {
            openDynamicModule("im", "nav")
        }
        binding.btH5.setOnClickListener {
            openDynamicModule("hotel-h5", "page")
        }
        binding.btNative1.setOnClickListener {
            toast("当前 bundle 内跳转尚未迁移到新 runtime")
        }
        binding.btNative2.setOnClickListener {
            toast("旧跨 bundle 路由尚未迁移到新 runtime")
        }
        binding.btReact.setOnClickListener {
            toast("React 动态模块入口尚未接入")
        }
        binding.btRn.setOnClickListener {
            toast("React Native 动态模块入口尚未接入")
        }
        binding.btAndroidNavigator.setOnClickListener {
            toast("Android navigator 示例尚未接入")
        }
//        binding.btNative1.setOnClickListener {
////            打开当前bundle内部的页面
//            IBCRouter.open(this) {
//                uri = "/ppp"
//            }
//        }
//        binding.btNative2.setOnClickListener {
//            //打开当前bundle内部的页面
//            IBCRouter.open(this) {
//                uri = "b://bundle1/sayme"
//            }
//        }
//        binding.btIm.setOnClickListener {
//            IBCRouter.open(this) {
//                uri = "b://im/nav"
//            }
//        }
//        binding.btReact.setOnClickListener {
//            //必须自定义router且bindingBundle=h5container
//            IBCRouter.open(this) {
//                uri = "https://spacecraft-plan.github.io/SpacecraftReact/#/"
//                params(
//                    "key2" to "cjf2",
//                    "key3" to 1,
//                    "key4" to true
//                )
//            }
//        }
//        binding.btH5.setOnClickListener {
//            IBCRouter.open(this) {
//                uri = "b://h5container/page"
//                params(
//                    "url" to "file:///android_asset/AApp.html",
//                )
//            }
//        }
//        binding.btRn.setOnClickListener {
//            IBCRouter.open(this) {
//                uri = "b://h5container/page"
//                params(
//                    "url" to "file:///android_asset/AApp.html",
//                )
//            }
//        }
    }

    private fun openDynamicModule(
        moduleName: String,
        entryPoint: String,
    ) {
        val manager = ModuleRuntimeRegistry.get()
        if (manager == null) {
            toast("Module runtime not initialized")
            return
        }
        manager.openModule(
            context = this,
            request = ModuleOpenRequest(
                moduleName = moduleName,
                entryPoint = entryPoint,
            ),
        ) { result ->
            runOnUiThread {
                when (result) {
                    is ModuleOpenResult.Opened -> {
                        toast("Opened $moduleName via ${result.installerId}")
                    }

                    is ModuleOpenResult.Failed -> {
                        toast(result.error.message)
                    }
                }
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
