package com.electrolytej.ad

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform