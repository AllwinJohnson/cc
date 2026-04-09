package org.example.cc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform