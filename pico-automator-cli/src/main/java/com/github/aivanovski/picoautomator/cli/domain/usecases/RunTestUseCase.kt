package com.github.aivanovski.picoautomator.cli.domain.usecases

import clojure.java.api.Clojure
import clojure.lang.Compiler
import clojure.lang.RT
import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import com.github.aivanovski.picoautomator.cli.entity.OutputFormat
import com.github.aivanovski.picoautomator.cli.entity.exception.PicoAutomatorException
import com.github.aivanovski.picoautomator.cli.utils.StringUtils.EMPTY
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowLifecycleListener
import com.github.aivanovski.picoautomator.domain.runner.FlowRunner
import com.github.aivanovski.picoautomator.domain.steps.FlowStep
import com.github.aivanovski.picoautomator.presentation.OutputWriter
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter.Companion.newCliReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter.Companion.newReplReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter.Companion.newSilentReporter
import java.io.StringReader
import java.lang.StringBuilder

class RunTestUseCase(
    private val fileProvider: FileProvider,
    private val writer: OutputWriter,
    private val resourceProvider: ResourceProvider
) {

    fun run(
        path: String,
        outputFormat: OutputFormat
    ): Either<Exception, Unit> {
        val loadApiResult = loadApiForPicoAutomator()
        if (loadApiResult.isLeft()) {
            return loadApiResult.mapToLeft()
        }

        val setupFlowResult = readAndSetupTestFlow(path)
        if (setupFlowResult.isLeft()) {
            return setupFlowResult.mapToLeft()
        }

        val flow = setupFlowResult.unwrap()
        return runTestFlow(flow, outputFormat)
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
            .replace("(print-stack-trace exception)", "(.printStackTrace exception)")

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

    private fun setupStaticData(
        collector: FlowResultCollector,
        outputFormat: OutputFormat
    ) {
        FlowRunner.defaultRunner.addLifecycleListener(collector)
        StandardOutputFlowReporter.defaultReporter = if (outputFormat == OutputFormat.DETAILED) {
            newCliReporter(writer)
        } else {
            newSilentReporter()
        }
    }

    private fun rollbackStaticData(collector: FlowResultCollector) {
        FlowRunner.defaultRunner.removeLifecycleListener(collector)
        StandardOutputFlowReporter.defaultReporter = newReplReporter(writer)
    }

    private fun runTestFlow(
        flow: ClojureTestFlow,
        outputFormat: OutputFormat
    ): Either<Exception, Unit> {
        val flowContent = flow.format()
        val collector = FlowResultCollector()

        setupStaticData(collector, outputFormat)

        try {
            Compiler.load(StringReader(flowContent))

            val foo = RT.`var`(flow.namespace, "-main")
            foo.invoke()
        } catch (exception: Exception) {
            rollbackStaticData(collector)
            return Either.Left(exception)
        }

        rollbackStaticData(collector)

        val error = collector.getFinishedFlows()
            .lastOrNull()
            ?.takeIf { (flow, result) -> result.isLeft() }
            ?.second

        return if (error == null) {
            Either.Right(Unit)
        } else {
            error.mapToLeft()
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

    class FlowResultCollector : FlowLifecycleListener {
        private val flowToResultMap = mutableMapOf<Flow, Either<Exception, Any>?>()

        fun getFinishedFlows(): List<Pair<Flow, Either<Exception, Any>>> {
            return flowToResultMap.entries
                .mapNotNull { entry ->
                    val flow = entry.key
                    val result = entry.value
                    if (result != null) {
                        Pair(flow, result)
                    } else {
                        null
                    }
                }
        }

        override fun onDeviceSelected(device: Device) {
        }

        override fun onFlowStarted(flow: Flow, isPredecessor: Boolean) {
            flowToResultMap[flow] = null
        }

        override fun onFlowFinished(flow: Flow, result: Either<Exception, Any>) {
            flowToResultMap[flow] = result
        }

        override fun onStepStarted(
            flow: Flow,
            step: FlowStep,
            stepIndex: Int,
            repeatCount: Int
        ) {
        }

        override fun onStepFinished(
            flow: Flow,
            step: FlowStep,
            stepIndex: Int,
            result: Either<Exception, Any>
        ) {
        }
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