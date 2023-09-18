package com.github.aivanovski.picoautomator.util

fun String.isDigitOnly(): Boolean {
    return this.all { it.isDigit() }
}