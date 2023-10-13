package com.github.aivanovski.picoautomator.presentation

class SilentOutputWriter : OutputWriter {
    override fun print(text: String) {}
    override fun println(text: String) {}
    override fun printStackTrace(exception: Exception) {}
}