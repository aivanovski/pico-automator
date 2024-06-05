package com.github.aivanovski.picoautomator.cli.domain

import com.github.aivanovski.picoautomator.cli.domain.argument.ArgumentParser
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintTestExecutionResultUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.RunTestsUseCase
import com.github.aivanovski.picoautomator.cli.entity.OutputFormat
import com.github.aivanovski.picoautomator.cli.entity.exception.TestExecutionException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.presentation.OutputWriter

class MainInteractor(
    private val argumentParser: ArgumentParser,
    private val printHelpUseCase: PrintHelpUseCase,
    private val runTestsUseCase: RunTestsUseCase,
    private val printResultUseCase: PrintTestExecutionResultUseCase,
    private val writer: OutputWriter
) {

    fun process(unparsedArgs: Array<String>): Either<Exception, Unit> {
        val parseArgsResult = argumentParser.parse(unparsedArgs)
        if (parseArgsResult.isLeft()) {
            return parseArgsResult.mapToLeft()
        }

        val args = parseArgsResult.unwrap()
        if (args.isPrintHelp || args.files.isEmpty()) {
            printHelpUseCase.printHelp(writer)
            return Either.Right(Unit)
        }

        val outputFormat = if (args.isNoStepsOutput) {
            OutputFormat.NO_STEPS
        } else {
            OutputFormat.DETAILED
        }

        val runTestsResult = runTestsUseCase.run(
            loadFiles = args.loadFiles,
            testFiles = args.files,
            outputFormat = outputFormat
        )
        if (runTestsResult.isLeft()) {
            return runTestsResult.mapToLeft()
        }

        val testsResult = runTestsResult.unwrap()
        printResultUseCase.printTestExecutionResult(testsResult)

        val isAllTestsPassed = testsResult.results.all { result -> result.isRight() }

        return if (isAllTestsPassed) {
            Either.Right(Unit)
        } else {
            Either.Left(TestExecutionException())
        }
    }
}