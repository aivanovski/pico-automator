package com.github.aivanovski.picoautomator.cli.domain.usecases

import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.domain.clojure.ClojureEngine
import com.github.aivanovski.picoautomator.cli.domain.clojure.ClojureTransformer
import com.github.aivanovski.picoautomator.cli.domain.clojure.ClojureTransformer.ClojureTestFlow
import com.github.aivanovski.picoautomator.cli.entity.OutputFormat
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

class RunTestUseCase(
    private val engine: ClojureEngine,
    private val transformer: ClojureTransformer,
    private val fileProvider: FileProvider,
    private val writer: OutputWriter
) {

    fun run(
        path: String,
        outputFormat: OutputFormat
    ): Either<Exception, Unit> {
        val readTestContentResult = fileProvider.read(path)
        if (readTestContentResult.isLeft()) {
            return readTestContentResult.mapToLeft()
        }

        val content = readTestContentResult.unwrap()
        val parseFlowResult = transformer.parseFlow(content)
        if (parseFlowResult.isLeft()) {
            return parseFlowResult.mapToLeft()
        }

        val flow = parseFlowResult.unwrap()

        return runTestFlow(flow, outputFormat)
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
        val collector = FlowResultCollector()

        val loadTestResult = engine.load(flow.formatContent())
        if (loadTestResult.isLeft()) {
            return loadTestResult.mapToLeft()
        }

        setupStaticData(collector, outputFormat)
        val runTestResult = engine.invoke(flow.namespace, "-main")
        rollbackStaticData(collector)

        if (runTestResult.isLeft()) {
            return runTestResult
        }

        val error = collector.getFinishedFlows()
            .lastOrNull()
            ?.takeIf { (flow, result) -> result.isLeft() }
            ?.second

        return error?.mapToLeft() ?: Either.Right(Unit)
    }

    private fun ClojureTestFlow.formatContent(): String {
        return "$namespaceBody\n$body"
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
}