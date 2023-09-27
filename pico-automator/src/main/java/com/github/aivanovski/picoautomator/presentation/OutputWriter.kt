package com.github.aivanovski.picoautomator.presentation

interface OutputWriter {
    fun print(text: String)
    fun println(text: String)
    fun printStackTrace(exception: Exception)
}