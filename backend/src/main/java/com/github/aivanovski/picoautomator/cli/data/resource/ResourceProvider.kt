package com.github.aivanovski.picoautomator.cli.data.resource

import com.github.aivanovski.picoautomator.domain.entity.Either

interface ResourceProvider {
    fun read(filename: String): Either<Exception, String>
}