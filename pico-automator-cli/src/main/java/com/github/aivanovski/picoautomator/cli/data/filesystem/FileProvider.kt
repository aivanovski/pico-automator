package com.github.aivanovski.picoautomator.cli.data.filesystem

import com.github.aivanovski.picoautomator.domain.entity.Either

interface FileProvider {
    fun exists(path: String): Boolean
    fun read(path: String): Either<Exception, String>
}