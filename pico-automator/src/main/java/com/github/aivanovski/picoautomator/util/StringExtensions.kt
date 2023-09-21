package com.github.aivanovski.picoautomator.util

internal fun String.isDigitOnly(): Boolean {
    return this.all { it.isDigit() }
}