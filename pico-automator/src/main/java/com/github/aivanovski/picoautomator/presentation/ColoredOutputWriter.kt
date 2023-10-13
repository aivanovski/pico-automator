package com.github.aivanovski.picoautomator.presentation

import com.github.aivanovski.picoautomator.presentation.entity.OutputColor

interface ColoredOutputWriter : OutputWriter {
    fun print(text: String, color: OutputColor)
    fun println(text: String, color: OutputColor)
}