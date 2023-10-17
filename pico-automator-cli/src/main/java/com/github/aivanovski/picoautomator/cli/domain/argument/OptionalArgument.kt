package com.github.aivanovski.picoautomator.cli.domain.argument

enum class OptionalArgument(
    private val shortName: String,
    private val fullName: String
) {

    LOAD(shortName = "l", fullName = "load"),
    NO_STEPS(shortName = "n", fullName = "no-steps"),
    HELP(shortName = "h", fullName = "help");

    val cliShortName: String = "-$shortName"
    val cliFullName: String = "--$fullName"
}