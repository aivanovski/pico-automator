package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.entity.Duration

fun Duration.toMilliseconds(): Long {
    return when (this) {
        is Duration.Seconds -> seconds * 1000L
        is Duration.Milliseconds -> milliseconds
    }
}

fun Duration.toReadableFormat(): String {
    return when (this) {
        is Duration.Seconds -> "$seconds seconds"
        is Duration.Milliseconds -> "$milliseconds millis"
    }
}