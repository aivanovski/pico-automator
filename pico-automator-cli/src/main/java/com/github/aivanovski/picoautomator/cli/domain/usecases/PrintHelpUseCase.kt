package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.presentation.OutputWriter

class PrintHelpUseCase(
    private val getVersionUseCase: GetVersionUseCase
) {

    fun printHelp(writer: OutputWriter) {
        writer.println(
            String.format(
                HELP_TEXT,
                getVersionUseCase.getVersionName()
            )
        )
    }

    companion object {
        internal val HELP_TEXT = """
            Pico Automator CLI %s

            USAGE:
                pico-automator-cli [OPTIONS] <FILE1> <FILE2> ...

            ARGS:
                <FILE-X>    Path to the file with test flow

            OPTIONS:
                -h, --help                       Print help information
        """.trimIndent()
    }
}