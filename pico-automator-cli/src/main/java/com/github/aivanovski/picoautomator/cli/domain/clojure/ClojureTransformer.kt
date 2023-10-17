package com.github.aivanovski.picoautomator.cli.domain.clojure

import com.github.aivanovski.picoautomator.cli.entity.exception.PicoAutomatorException
import com.github.aivanovski.picoautomator.cli.utils.StringUtils
import com.github.aivanovski.picoautomator.domain.entity.Either
import java.lang.StringBuilder

class ClojureTransformer {

    fun replacePrintStackTrace(content: String): String {
        return content
            .replace("(print-stack-trace exception)", "(.printStackTrace exception)")
    }

    fun parseFlow(content: String): Either<Exception, ClojureTestFlow> {
        val (namespace, body) = splitFlowIntoNamespaceAndBody(content)
        return setupFlowContent(namespace, body)
    }

    private fun splitFlowIntoNamespaceAndBody(flow: String): Pair<String?, String> {
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
            initialNamespaceContent ?: StringUtils.EMPTY
        }

        val namespace = extractNamespacePath(namespaceContent)
            ?: return Either.Left(PicoAutomatorException("Failed to parse namespace"))

        return Either.Right(
            ClojureTestFlow(
                namespace = namespace,
                namespaceBody = namespaceContent,
                body = content
            )
        )
    }

    data class ClojureTestFlow(
        val namespace: String,
        val namespaceBody: String,
        val body: String
    )

    companion object {
        private const val NAMESPACE_DECLARATION = "(ns"
        private val NAMESPACE_STOP_CHARACTERS = setOf(
            ' ', '\n', '\r', '\t', '(', ')', '[', ']', '{', '}', ':'
        )
    }
}