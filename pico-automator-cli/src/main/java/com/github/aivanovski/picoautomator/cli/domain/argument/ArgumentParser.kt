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
                    isPrintHelp = false
                )
            )
        }

        val files = mutableListOf<String>()
        var isPrintHelp = false

        val queue = LinkedList(args.toList())
        while (queue.isNotEmpty()) {
            val arg = queue.poll()
            if (arg.startsWith("-") || arg.startsWith("--")) {
                when (OPTIONS_ARGUMENTS_MAP[arg]) {
                    OptionalArgument.HELP -> isPrintHelp = true
                    else -> {
                        return Either.Left(
                            ParsingException(String.format(UNKNOWN_OPTION, arg))
                        )
                    }
                }

            } else {
                val checkPathResult = checkPath(arg)
                if (checkPathResult.isLeft()) {
                    return checkPathResult.mapToLeft()
                }

                files.add(arg)
            }
        }

        return Either.Right(
            Arguments(
                files = files,
                isPrintHelp = isPrintHelp
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
            OptionalArgument.HELP.cliFullName to OptionalArgument.HELP
        )
    }
}