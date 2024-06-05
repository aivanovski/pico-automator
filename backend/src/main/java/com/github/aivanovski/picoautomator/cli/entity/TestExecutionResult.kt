package com.github.aivanovski.picoautomator.cli.entity

import com.github.aivanovski.picoautomator.domain.entity.Either

data class TestExecutionResult(
    val files: List<String>,
    val results: List<Either<Exception, Unit>>,
    val time: Long
)