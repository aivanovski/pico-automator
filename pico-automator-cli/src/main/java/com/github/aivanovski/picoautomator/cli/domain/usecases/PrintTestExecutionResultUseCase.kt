package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.entity.TestExecutionResult
import com.github.aivanovski.picoautomator.presentation.ColoredOutputWriter
import com.github.aivanovski.picoautomator.presentation.entity.OutputColor

class PrintTestExecutionResultUseCase(
    private val writer: ColoredOutputWriter
) {

    fun printTestExecutionResult(executionResult: TestExecutionResult) {
        val allTests = executionResult.results.size

        val passed = executionResult.results.count { result -> result.isRight() }
        val failed = executionResult.results.count { result -> result.isLeft() }
        if (failed == 0) {
            writer.print("Tests passed:", OutputColor.GREEN)
            writer.println(" $passed of $allTests")
        } else {
            writer.print("Tests failed: $failed", OutputColor.RED)
            writer.println(", passed: $passed of $allTests")
        }
    }
}