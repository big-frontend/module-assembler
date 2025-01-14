package com.electrolytej.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform