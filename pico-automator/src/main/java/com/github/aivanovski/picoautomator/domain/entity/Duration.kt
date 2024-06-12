package com.github.aivanovski.picoautomator.domain.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface Duration {

    @Serializable
    data class Seconds(val seconds: Int) : Duration

    @Serializable
    data class Milliseconds(val milliseconds: Long) : Duration

    companion object {

        @JvmStatic
        fun millis(milliseconds: Int) = Milliseconds(milliseconds.toLong())

        @JvmStatic
        fun millis(milliseconds: Long) = Milliseconds(milliseconds)

        @JvmStatic
        fun seconds(seconds: Int) = Seconds(seconds)
    }
}