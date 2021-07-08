package com.jamesfchen.ibc

import com.jamesfchen.ibc.route.IBCRouter
import org.junit.Test

import org.junit.Assert.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        val e = URLEncoder.encode("Dankeschön für Ihre €100", StandardCharsets.UTF_8.name())
        val e = URLEncoder.encode("举个栗子", StandardCharsets.UTF_8.name())
        IBCRouter.goto("native://hotel-bundle1/sayme/c?from=${e}")
        assertEquals(4, 2 + 2)
    }
}