package com.github.aivanovski.picoautomator.cli.domain.argument

import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.domain.Strings.FILE_DOES_NOT_EXIST
import com.github.aivanovski.picoautomator.cli.domain.Strings.UNKNOWN_OPTION
import com.github.aivanovski.picoautomator.cli.entity.Arguments
import com.github.aivanovski.picoautomator.cli.entity.exception.ParsingException
import com.github.aivanovski.picoautomator.domain.entity.Either
import java.util.LinkedList

class ArgumentParser(
    private val fileProvider: FileProvider
) {

    fun parse(args: Array<String>): Either<ParsingException, Arguments> {
        if (args.isEmpty()) {
            return Either.Right(
                Arguments(
                    files = emptyList(),
                    loadFiles = emptyList(),
                    isPrintHelp = false,
                    isNoStepsOutput = false
                )
            )
        }

        val files = mutableListOf<String>()
        val loadFiles = mutableListOf<String>()
        var isPrintHelp = false
        var isNoStepsOutput = false

        val queue = LinkedList(args.toList())
        while (queue.isNotEmpty()) {
            val arg = queue.poll()
            if (arg.startsWith("-") || arg.startsWith("--")) {
                when (OPTIONS_ARGUMENTS_MAP[arg]) {
                    OptionalArgument.HELP -> isPrintHelp = true
                    OptionalArgument.NO_STEPS -> isNoStepsOutput = true
                    OptionalArgument.LOAD -> {
                        val file = queue.poll()
                        val checkPathResult = checkPath(file)
                        if (checkPathResult.isLeft()) {
                            return checkPathResult.toLeft()
                        }

                        loadFiles.add(file)
                    }
                    else -> {
                        return Either.Left(
                            ParsingException(String.format(UNKNOWN_OPTION, arg))
                        )
                    }
                }
            } else {
                val checkPathResult = checkPath(arg)
                if (checkPathResult.isLeft()) {
                    return checkPathResult.toLeft()
                }

                files.add(arg)
            }
        }

        return Either.Right(
            Arguments(
                files = files,
                loadFiles = loadFiles,
                isPrintHelp = isPrintHelp,
                isNoStepsOutput = isNoStepsOutput
            )
        )
    }

    private fun checkPath(path: String?): Either<ParsingException, String> {
        if (path.isNullOrEmpty() || !fileProvider.exists(path)) {
            return Either.Left(
                ParsingException(String.format(FILE_DOES_NOT_EXIST, path))
            )
        }

        return Either.Right(path)
    }

    companion object {
        private val OPTIONS_ARGUMENTS_MAP = mapOf(
            OptionalArgument.HELP.cliShortName to OptionalArgument.HELP,
            OptionalArgument.HELP.cliFullName to OptionalArgument.HELP,

            OptionalArgument.NO_STEPS.cliShortName to OptionalArgument.NO_STEPS,
            OptionalArgument.NO_STEPS.cliFullName to OptionalArgument.NO_STEPS,

            OptionalArgument.LOAD.cliShortName to OptionalArgument.LOAD,
            OptionalArgument.LOAD.cliFullName to OptionalArgument.LOAD
        )
    }
}