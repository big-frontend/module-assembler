package com.electrolytej.main

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform