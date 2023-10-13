package com.github.aivanovski.picoautomator.cli.domain.argument

enum class OptionalArgument(
    private val shortName: String,
    private val fullName: String
) {
    HELP(shortName = "h", fullName = "help"),
    NO_STEPS(shortName = "n", fullName = "no-steps");

    val cliShortName: String = "-$shortName"
    val cliFullName: String = "--$fullName"
}