package com.github.aivanovski.picoautomator.cli.domain.clojure

import clojure.java.api.Clojure
import clojure.lang.Compiler
import clojure.lang.RT
import com.github.aivanovski.picoautomator.domain.entity.Either
import java.io.StringReader

class ClojureEngine {

    private var isInitialized = false

    private fun initialize(): Either<Exception, Unit> {
        return try {
            Clojure.`var`("clojure.core", "require")
            isInitialized = true

            Either.Right(Unit)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }

    fun load(content: String): Either<Exception, Unit> {
        if (isInitialized) {
            initialize()
        }

        return try {
            Clojure.`var`("clojure.core", "require")

            Compiler.load(StringReader(content))

            Either.Right(Unit)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }

    fun invoke(namespace: String, functionName: String): Either<Exception, Unit> {
        if (isInitialized) {
            initialize()
        }

        return try {
            val foo = RT.`var`(namespace, functionName)
            foo.invoke()

            Either.Right(Unit)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }
}