package com.github.aivanovski.picoautomator.cli.entity

data class Arguments(
    val files: List<String>,
    val loadFiles: List<String>,
    val isPrintHelp: Boolean,
    val isNoStepsOutput: Boolean
)