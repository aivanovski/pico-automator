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
            pico-automator CLI %s
            The CLI-tool to run tests written in Clojure with pico-automator library

            USAGE:
                pico-automator [OPTIONS] [FILES]

            DESCRIPTION:
                FILES stand for one or more .clj files with test

            OPTIONS:
                -n, --no-step                    Hides output information about flow steps
                -h, --help                       Print help information
        """.trimIndent()
    }
}