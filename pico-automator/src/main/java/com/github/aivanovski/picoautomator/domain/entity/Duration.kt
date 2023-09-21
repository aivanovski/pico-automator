package com.github.aivanovski.picoautomator.domain.entity

sealed class Duration(open val milliseconds: Long) {

    data class Seconds(val seconds: Int) : Duration(seconds * 1000L)
    data class Milliseconds(override val milliseconds: Long) : Duration(milliseconds)

    companion object {
        fun millis(milliseconds: Int) = Milliseconds(milliseconds.toLong())
        fun seconds(seconds: Int) = Seconds(seconds)
    }
}