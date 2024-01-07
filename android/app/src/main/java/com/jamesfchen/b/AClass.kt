package com.jamesfchen.b


import HELLO
import com.jamesfchen.ibc.Builder

@Builder
class AClass(private val a: Int, val b: String, val c: Double, val d: HELLO) {
    val p = "$a, $b, $c, ${d.foo()}"
    fun foo() = p
}