package com.github.aivanovski.picoautomator.cli.entity.exception

open class PicoAutomatorException : Exception {
    constructor(message: String) : super(message)
    constructor(reason: Exception) : super(reason.message)
}