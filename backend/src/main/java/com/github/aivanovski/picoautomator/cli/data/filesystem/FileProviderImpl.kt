package com.github.aivanovski.picoautomator.cli.data.filesystem

import com.github.aivanovski.picoautomator.domain.entity.Either
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class FileProviderImpl : FileProvider {

    override fun exists(path: String): Boolean {
        return File(path).exists()
    }

    override fun read(path: String): Either<Exception, String> {
        return try {
            val reader = InputStreamReader(FileInputStream(File(path)))
            Either.Right(reader.readText())
        } catch (exception: IOException) {
            Either.Left(exception)
        }
    }
}