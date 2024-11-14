package com.tamurasouko.twics.multiplatform_zaico_sample

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}