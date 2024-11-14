package com.tamurasouko.twics.multiplatform_zaico_sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform