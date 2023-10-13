package com.github.aivanovski.picoautomator.utils

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowLifecycleListener
import com.github.aivanovski.picoautomator.domain.steps.FlowStep
import com.github.aivanovski.picoautomator.presentation.OutputWriter
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputWriter
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.StringBuilder

class CapturingTestFlowReporter(
    private val onFinished: () -> Unit
) : FlowLifecycleListener {

    private val outputWriter = TestOutputWriter()
    private val reporter = StandardOutputFlowReporter(
        outputWriter,
        isPrintStackTraceOnError = false
    )

    override fun onDeviceSelected(device: Device) {
        reporter.onDeviceSelected(device)
    }

    override fun onFlowStarted(flow: Flow, isPredecessor: Boolean) {
        reporter.onFlowStarted(flow, isPredecessor)
    }

    override fun onFlowFinished(flow: Flow, result: Either<Exception, Any>) {
        reporter.onFlowFinished(flow, result)
        onFinished.invoke()
    }

    override fun onStepStarted(flow: Flow, step: FlowStep, stepIndex: Int, repeatCount: Int) {
        reporter.onStepStarted(flow, step, stepIndex, repeatCount)
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<Exception, Any>
    ) {
        reporter.onStepFinished(flow, step, stepIndex, result)
    }

    fun getCapturedOutput(): String {
        return outputWriter.output.toString()
    }

    private class TestOutputWriter : OutputWriter {

        val output = StringBuilder()
        private val writer = StandardOutputWriter()
        private val exceptionOutputStream = ByteArrayOutputStream(12 * 1024)

        override fun print(text: String) {
            output.append(text)
            writer.print(text)
        }

        override fun println(text: String) {
            output.append(text).append("\n")
            writer.println(text)
        }

        override fun printStackTrace(exception: Exception) {
            exceptionOutputStream.reset()
            exception.printStackTrace(PrintStream(exceptionOutputStream))
            output.append(exceptionOutputStream.toString())
            writer.printStackTrace(exception)
        }
    }
}