package com.github.aivanovski.picoautomator.data.resources

import com.github.aivanovski.picoautomator.domain.entity.Either
import java.io.IOException

interface ResourceProvider {
    fun read(filename: String): Either<IOException, String>
}