package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.entity.OutputFormat
import com.github.aivanovski.picoautomator.cli.entity.TestExecutionResult
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.presentation.OutputWriter

class RunTestsUseCase(
    private val runTestUseCase: RunTestUseCase,
    private val writer: OutputWriter
) {

    fun run(
        files: List<String>,
        outputFormat: OutputFormat
    ): TestExecutionResult {
        val results = mutableListOf<Either<Exception, Unit>>()

        val started = System.currentTimeMillis()
        for (file in files) {
            val result = runTestUseCase.run(file, outputFormat)

            if (result.isLeft()) {
                writer.printStackTrace(result.unwrapError())
            }

            results.add(result)
        }

        val ended = System.currentTimeMillis()

        return TestExecutionResult(
            files = files,
            results = results,
            time = (ended - started)
        )
    }
}