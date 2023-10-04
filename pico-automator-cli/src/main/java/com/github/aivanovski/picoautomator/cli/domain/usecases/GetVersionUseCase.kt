package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import java.io.StringReader
import java.util.Properties

class GetVersionUseCase(
    private val resourceProvider: ResourceProvider
) {

    fun getVersionName(): String {
        val getVersionFileResult = resourceProvider.read(VERSION_PROPERTY_FILE_NAME)
        if (getVersionFileResult.isLeft()) {
            throw getVersionFileResult.unwrapError()
        }

        val content = getVersionFileResult.unwrap()
        val properties = Properties()
            .apply {
                load(StringReader(content))
            }

        return properties[PROPERTY_VERSION] as String
    }

    companion object {
        internal const val VERSION_PROPERTY_FILE_NAME = "version.properties"
        internal const val PROPERTY_VERSION = "version"
    }
}