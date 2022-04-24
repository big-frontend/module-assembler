package com.jamesfchen.bundle2

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.perf.metrics.AddTrace
import com.jamesfchen.export.ICall
import com.jamesfchen.ibc.cbpc.IBCCbpc
import com.jamesfchen.ibc.router.IBCRouter

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jun/19/2021  Sat
 */
//@Route(path = "/bundle2/sayhi")
class SayHiActivity : Activity() {
    @AddTrace(name = "SayHiActivity_onCreate", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = Button(this)
        tv.text = "say hi"
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.BLACK)
        tv.isAllCaps = false
        setContentView(
            tv,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        tv.setOnClickListener { // 1. Simple jump within application (Jump via URL in 'Advanced usage')
//                ARouter.getInstance().build("/bundle1/sayme").navigation();
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("jamesfchen://www.jamesfchen.com/hotel/main"));
//                startActivity(intent);
            // 2. Jump with parameters
//                    ARouter.getInstance().build("/login/1")
//                            .withLong("key1", 666L)
//                            .withString("key3", "888")
//                            .navigation();
            val api = IBCCbpc.findApi(ICall::class.java)
            if (api?.call() == true) {
                IBCRouter.open(this) {
                    uri = "b://h5container/page"
                    params(
//                        "url" to "file:///android_asset/AApp.html",
                        "url" to "https://spacecraft-plan.github.io/SpacecraftReact/#/",
                        "key2" to "cjf2",
                        "key3" to 1,
                        "key4" to true
                    )
                }
            }
        }
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                Log.w("cjf", "deepLink:$deepLink")


                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w("cjf", "getDynamicLink:onFailure", e) }
    }
}