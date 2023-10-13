package com.github.aivanovski.picoautomator.presentation

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowLifecycleListener
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

class StandardOutputFlowReporter(
    private val writer: OutputWriter,
    private val isPrintStackTraceOnError: Boolean
) : FlowLifecycleListener {

    override fun onDeviceSelected(device: Device) {
        writer.println("Select device: ${device.id}")
    }

    override fun onFlowStarted(flow: Flow, isPredecessor: Boolean) {
        if (isPredecessor) {
            writer.println("Start predecessor flow '${flow.name}'")
        } else {
            writer.println("Start flow '${flow.name}'")
        }
    }

    override fun onFlowFinished(flow: Flow, result: Either<Exception, Any>) {
        if (result.isRight()) {
            val data = result.unwrap()
            if (data is String) {
                writer.println("Flow '${flow.name}' finished successfully: $data")
            } else {
                writer.println("Flow '${flow.name}' finished successfully")
            }
        } else {
            val exception = result.unwrapError()
            writer.println(
                "Flow '${flow.name}' failed: ${exception.message ?: exception.javaClass.simpleName}"
            )
            if (isPrintStackTraceOnError) {
                writer.printStackTrace(exception)
            }
        }
    }

    override fun onStepStarted(flow: Flow, step: FlowStep, stepIndex: Int, repeatCount: Int) {
        if (repeatCount == 0) {
            writer.print("Step ${stepIndex + 1}: ${step.describe()}")
        } else {
            writer.print("Retry ${stepIndex + 1}: ${step.describe()}")
        }
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<Exception, Any>
    ) {
        val resultMessage = if (result.isLeft()) "FAILED" else "SUCCESS"
        writer.println(" - $resultMessage")
    }

    companion object {

        @JvmStatic
        var defaultReporter: StandardOutputFlowReporter? = null

        @JvmStatic
        fun newReplReporter(writer: OutputWriter): StandardOutputFlowReporter {
            return StandardOutputFlowReporter(
                writer,
                isPrintStackTraceOnError = true
            )
        }

        @JvmStatic
        fun newCliReporter(writer: OutputWriter): StandardOutputFlowReporter {
            return StandardOutputFlowReporter(
                writer,
                isPrintStackTraceOnError = false
            )
        }

        @JvmStatic
        fun newSilentReporter(): StandardOutputFlowReporter {
            return StandardOutputFlowReporter(
                SilentOutputWriter(),
                isPrintStackTraceOnError = false
            )
        }
    }
}