package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.entity.Duration

internal fun Duration.toReadableFormat(): String {
    return when (this) {
        is Duration.Seconds -> "$seconds seconds"
        is Duration.Milliseconds -> "$milliseconds millis"
    }
}