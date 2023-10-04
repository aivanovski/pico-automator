package com.github.aivanovski.picoautomator.cli.domain.usecases

import clojure.java.api.Clojure
import clojure.lang.Compiler
import clojure.lang.RT
import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import com.github.aivanovski.picoautomator.cli.entity.exception.PicoAutomatorException
import com.github.aivanovski.picoautomator.cli.utils.StringUtils.EMPTY
import com.github.aivanovski.picoautomator.domain.entity.Either
import java.io.StringReader
import java.lang.StringBuilder

class RunTestUseCase(
    private val fileProvider: FileProvider,
    private val resourceProvider: ResourceProvider
) {

    fun run(path: String): Either<Exception, Unit> {
        val loadApiResult = loadApiForPicoAutomator()
        if (loadApiResult.isLeft()) {
            return loadApiResult.mapToLeft()
        }

        val setupFlowResult = readAndSetupTestFlow(path)
        if (setupFlowResult.isLeft()) {
            return setupFlowResult.mapToLeft()
        }

        val flow = setupFlowResult.unwrap()
        return runTestFlow(flow)
    }

    private fun loadApiForPicoAutomator(): Either<Exception, Unit> {
        val readUtilsResult = resourceProvider.read("picoautomator/utils.clj")
        if (readUtilsResult.isLeft()) {
            return readUtilsResult.mapToLeft()
        }

        val readCoreResult = resourceProvider.read("picoautomator/core.clj")
        if (readCoreResult.isLeft()) {
            return readCoreResult.mapToLeft()
        }

        val utils = readUtilsResult.unwrap()
        val core = readCoreResult.unwrap()

        return try {
            Clojure.`var`("clojure.core", "require")

            Compiler.load(StringReader(utils))
            Compiler.load(StringReader(core))

            Either.Right(Unit)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }

    private fun readAndSetupTestFlow(path: String): Either<Exception, ClojureTestFlow> {
        val readFileResult = fileProvider.read(path)
        if (readFileResult.isLeft()) {
            return readFileResult.mapToLeft()
        }

        val initialFlowContent = readFileResult.unwrap()
        val (namespace, content) = splitFlowIntoNamespaceAndContent(initialFlowContent)
        return setupFlowContent(namespace, content)
    }

    private fun runTestFlow(flow: ClojureTestFlow): Either<Exception, Unit> {
        val flowContent = flow.format()

        return try {
            Compiler.load(StringReader(flowContent))

            val foo = RT.`var`(flow.namespace, "-main")
            foo.invoke()
            Either.Right(Unit)
        } catch (exception: Exception) {
            Either.Left(exception)
        }
    }

    private fun splitFlowIntoNamespaceAndContent(flow: String): Pair<String?, String> {
        val nsIndex = flow.indexOf(NAMESPACE_DECLARATION)
        if (nsIndex == -1) {
            return Pair(null, flow)
        }

        var openBracketCount = 1
        var idx = nsIndex + 1
        while (idx < flow.length) {
            when (flow[idx]) {
                '(' -> openBracketCount++
                ')' -> openBracketCount--
            }

            if (openBracketCount == 0) {
                break
            }
            idx++
        }

        return if (openBracketCount == 0) {
            val namespace = flow.substring(nsIndex, idx + 1).trim()
            val content = flow.substring(idx + 1).trim()
            Pair(namespace, content)
        } else {
            Pair(null, flow)
        }
    }

    private fun extractNamespacePath(namespace: String): String? {
        val nsIndex = namespace.indexOf(NAMESPACE_DECLARATION)
        if (nsIndex == -1) {
            return null
        }

        val startIdx = nsIndex + NAMESPACE_DECLARATION.length + 1
        var endIdx = startIdx
        while (endIdx < namespace.length) {
            if (namespace[endIdx] in NAMESPACE_STOP_CHARACTERS) {
                break
            }

            endIdx++
        }

        if (startIdx >= namespace.length ||
            endIdx > namespace.length ||
            endIdx <= startIdx
        ) {
            return null
        }

        return namespace.substring(startIdx, endIdx).trim()
    }

    private fun setupFlowContent(
        initialNamespaceContent: String?,
        initialContent: String
    ): Either<Exception, ClojureTestFlow> {
        val hasNamespace = (initialNamespaceContent != null)
        val hasMainFunction = initialContent.contains("(defn -main")

        val content = if (!hasMainFunction) {
            StringBuilder()
                .apply {
                    append("(defn -main [& args]\n")
                    append(initialContent)
                    append(")\n")
                }
                .toString()
        } else {
            initialContent
        }

        val namespaceContent = if (!hasNamespace) {
            """
            (ns test
              (:require [picoautomator.core :refer :all]))
            """.trimIndent()
        } else {
            initialNamespaceContent ?: EMPTY
        }

        val namespace = extractNamespacePath(namespaceContent)
            ?: return Either.Left(PicoAutomatorException("Failed to parse namespace"))

        return Either.Right(
            ClojureTestFlow(
                namespace = namespace,
                namespaceContent = namespaceContent,
                content = content
            )
        )
    }

    private fun ClojureTestFlow.format(): String {
        return "$namespaceContent\n$content"
    }

    private data class ClojureTestFlow(
        val namespace: String?,
        val namespaceContent: String,
        val content: String
    )

    companion object {
        private const val NAMESPACE_DECLARATION = "(ns"
        private val NAMESPACE_STOP_CHARACTERS = setOf(
            ' ', '\n', '\r', '\t', '(', ')', '[', ']', '{', '}', ':'
        )
    }
}