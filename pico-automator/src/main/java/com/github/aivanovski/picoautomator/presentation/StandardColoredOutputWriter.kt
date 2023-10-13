package com.github.aivanovski.picoautomator.presentation

import com.github.aivanovski.picoautomator.presentation.entity.OutputColor

class StandardColoredOutputWriter(
    private val writer: OutputWriter
) : ColoredOutputWriter {

    override fun print(text: String) = writer.print(text)
    override fun println(text: String) = writer.println(text)
    override fun printStackTrace(exception: Exception) = writer.printStackTrace(exception)

    override fun print(text: String, color: OutputColor) {
        writer.print(formatColoredText(text, color))
    }

    override fun println(text: String, color: OutputColor) {
        writer.println(formatColoredText(text, color))
    }

    private fun formatColoredText(text: String, color: OutputColor): String {
        return if (color != OutputColor.NONE) {
            "${color.getColorCode()}$text${OutputColor.DEFAULT.getColorCode()}"
        } else {
            text
        }
    }

    private fun OutputColor.getColorCode(): String {
        return when (this) {
            OutputColor.NONE -> ""
            OutputColor.DEFAULT -> "\u001B[0m"
            OutputColor.RED -> "\u001B[31m"
            OutputColor.GREEN -> "\u001B[32m"
        }
    }
}