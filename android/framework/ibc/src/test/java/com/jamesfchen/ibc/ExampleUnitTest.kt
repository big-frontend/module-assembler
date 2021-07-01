package com.jamesfchen.ibc

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val r = routerConfig
        println("ExampleUnitTest ${r}")
        assertEquals(4, 2 + 2)
    }
}