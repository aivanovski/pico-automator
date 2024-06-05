package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.entity.OutputFormat
import com.github.aivanovski.picoautomator.cli.entity.TestExecutionResult
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.presentation.OutputWriter

class RunTestsUseCase(
    private val setupEnvironmentUseCase: SetupTestEnvironmentUseCase,
    private val runTestUseCase: RunTestUseCase,
    private val writer: OutputWriter
) {

    fun run(
        loadFiles: List<String>,
        testFiles: List<String>,
        outputFormat: OutputFormat
    ): Either<Exception, TestExecutionResult> {
        val setupEnvironmentResult = setupEnvironmentUseCase.setupClojureEnvironment(loadFiles)
        if (setupEnvironmentResult.isLeft()) {
            return setupEnvironmentResult.mapToLeft()
        }

        val results = mutableListOf<Either<Exception, Unit>>()

        val started = System.currentTimeMillis()
        for (file in testFiles) {
            val result = runTestUseCase.run(file, outputFormat)

            if (result.isLeft()) {
                writer.printStackTrace(result.unwrapError())
            }

            results.add(result)
        }

        val ended = System.currentTimeMillis()

        return Either.Right(
            TestExecutionResult(
                files = testFiles,
                results = results,
                time = (ended - started)
            )
        )
    }
}