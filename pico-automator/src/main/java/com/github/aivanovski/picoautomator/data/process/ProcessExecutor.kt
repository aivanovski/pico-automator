package com.github.aivanovski.picoautomator.data.process

import com.github.aivanovski.picoautomator.domain.entity.Either
import org.buildobjects.process.ProcBuilder

class ProcessExecutor {

    fun run(
        command: String
    ): Either<Exception, String> {
        return executeInternal(
            input = null,
            command = "bash",
            arguments = listOf("-c", command)
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
}