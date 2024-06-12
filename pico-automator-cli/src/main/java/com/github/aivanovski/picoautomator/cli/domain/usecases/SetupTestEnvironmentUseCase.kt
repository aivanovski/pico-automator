package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import com.github.aivanovski.picoautomator.cli.domain.clojure.ClojureEngine
import com.github.aivanovski.picoautomator.cli.domain.clojure.ClojureTransformer
import com.github.aivanovski.picoautomator.domain.entity.Either

class SetupTestEnvironmentUseCase(
    private val engine: ClojureEngine,
    private val transformer: ClojureTransformer,
    private val fileProvider: FileProvider,
    private val resourceProvider: ResourceProvider
) {

    fun setupClojureEnvironment(
        loadFiles: List<String>
    ): Either<Exception, Unit> {
        val loadApiResult = loadApiForPicoAutomator()
        if (loadApiResult.isLeft()) {
            return loadApiResult.toLeft()
        }

        for (file in loadFiles) {
            val loadFileResult = loadFile(file)
            if (loadFileResult.isLeft()) {
                return loadFileResult.toLeft()
            }
        }

        return Either.Right(Unit)
    }

    private fun loadApiForPicoAutomator(): Either<Exception, Unit> {
        val readUtilsResult = resourceProvider.read("picoautomator/utils.clj")
        if (readUtilsResult.isLeft()) {
            return readUtilsResult.toLeft()
        }

        val readCoreResult = resourceProvider.read("picoautomator/core.clj")
        if (readCoreResult.isLeft()) {
            return readCoreResult.toLeft()
        }

        val utilsContent = transformer.replacePrintStackTrace(readUtilsResult.unwrap())
        val coreContent = readCoreResult.unwrap()

        val loadUtilResult = engine.load(utilsContent)
        if (loadUtilResult.isLeft()) {
            return loadUtilResult.toLeft()
        }

        val loadCoreResult = engine.load(coreContent)
        if (loadCoreResult.isLeft()) {
            return loadCoreResult.toLeft()
        }

        return Either.Right(Unit)
    }

    private fun loadFile(path: String): Either<Exception, Unit> {
        val readFileResult = fileProvider.read(path)
        if (readFileResult.isLeft()) {
            return readFileResult.toLeft()
        }

        val fileContent = readFileResult.unwrap()
        return engine.load(fileContent)
    }
}