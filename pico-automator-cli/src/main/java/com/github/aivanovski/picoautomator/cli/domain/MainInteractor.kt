package com.github.aivanovski.picoautomator.cli.domain

import com.github.aivanovski.picoautomator.cli.domain.argument.ArgumentParser
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.RunTestUseCase
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.presentation.OutputWriter

class MainInteractor(
    private val argumentParser: ArgumentParser,
    private val printHelpUseCase: PrintHelpUseCase,
    private val runTestUseCase: RunTestUseCase,
    private val errorHandler: ErrorHandler,
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

        for (file in args.files) {
            val result = runTestUseCase.run(file)
            if (result.isLeft()) {
                errorHandler.processIfLeft(result)
            }
        }

        return Either.Right(Unit)
    }
}