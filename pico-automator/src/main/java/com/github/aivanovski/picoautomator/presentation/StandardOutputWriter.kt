package com.github.aivanovski.picoautomator.presentation

class StandardOutputWriter : OutputWriter {

    override fun print(text: String) {
        kotlin.io.print(text)
    }

    override fun println(text: String) {
        kotlin.io.println(text)
    }

    override fun printStackTrace(exception: Exception) {
        exception.printStackTrace()
    }
}