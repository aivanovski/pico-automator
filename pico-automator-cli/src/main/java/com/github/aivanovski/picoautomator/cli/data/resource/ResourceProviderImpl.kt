package com.github.aivanovski.picoautomator.cli.data.resource

import com.github.aivanovski.picoautomator.domain.entity.Either
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class ResourceProviderImpl : ResourceProvider {

    override fun read(filename: String): Either<Exception, String> {
        return try {
            val content = ResourceProviderImpl::class.java.classLoader
                .getResourceAsStream(filename)

            if (content != null) {
                Either.Right(InputStreamReader(content).readText())
            } else {
                Either.Left(FileNotFoundException(filename))
            }
        } catch (exception: IOException) {
            Either.Left(exception)
        }
    }
}