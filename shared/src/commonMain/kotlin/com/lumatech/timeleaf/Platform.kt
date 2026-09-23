package com.lumatech.timeleaf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform