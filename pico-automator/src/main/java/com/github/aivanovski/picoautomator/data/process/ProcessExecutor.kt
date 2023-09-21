package com.github.aivanovski.picoautomator.data.process

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.util.StringUtils.SPACE
import org.buildobjects.process.ProcBuilder

internal class ProcessExecutor {

    fun run(
        command: String
    ): Either<Exception, String> {
        val (com, args) = command.splitIntoCommandAndArgs()
        return executeInternal(
            input = null,
            command = com,
            arguments = args
        )
    }

    private fun executeInternal(
        input: ByteArray?,
        command: String,
        arguments: List<String>
    ): Either<Exception, String> {
        return try {
            val builder = ProcBuilder(command, *arguments.toTypedArray())

            if (input != null) {
                builder.withInput(input)
            }

            Either.Right(builder.run().outputString)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }

    private fun String.splitIntoCommandAndArgs(): Pair<String, List<String>> {
        if (this.isBlank()) {
            return Pair(this, emptyList())
        }

        if (!this.contains(SPACE)) {
            return Pair(this, emptyList())
        }

        val values = this.split(SPACE)

        return when {
            values.size == 1 -> Pair(values.first(), emptyList())
            values.size > 1 -> Pair(values.first(), values.subList(1, values.size))
            else -> Pair(this, emptyList())
        }
    }
}