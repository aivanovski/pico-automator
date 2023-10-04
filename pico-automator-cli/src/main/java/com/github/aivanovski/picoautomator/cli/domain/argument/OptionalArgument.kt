package com.github.aivanovski.picoautomator.cli.domain.argument

enum class OptionalArgument(
    private val shortName: String,
    private val fullName: String,
) {
    HELP(shortName = "h", fullName = "help");

    val cliShortName: String = "-$shortName"
    val cliFullName: String = "--$fullName"
}