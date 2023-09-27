package com.github.aivanovski.picoautomator.presentation

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowLifecycleListener
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

class StandardOutputFlowReporter(
    private val output: OutputWriter
) : FlowLifecycleListener {

    override fun onDeviceSelected(device: Device) {
        output.println("Select device: ${device.id}")
    }

    override fun onFlowStarted(flow: Flow, isPredecessor: Boolean) {
        if (isPredecessor) {
            output.println("Run predecessor flow: ${flow.name}")
        } else {
            output.println("Run flow: ${flow.name}")
        }
    }

    override fun onFlowFinished(flow: Flow, result: Either<Exception, Any>) {
        if (result.isRight()) {
            val data = result.unwrap()
            if (data is String) {
                output.println("Flow '${flow.name}' finished successfully: $data")
            } else {
                output.println("Flow '${flow.name}' finished successfully")
            }
        } else {
            val exception = result.unwrapError()
            output.println(
                "Flow '${flow.name}' failed: ${exception.message ?: exception.javaClass.simpleName}"
            )
            output.printStackTrace(exception)
        }
    }

    override fun onStepStarted(flow: Flow, step: FlowStep, stepIndex: Int, repeatCount: Int) {
        if (repeatCount == 0) {
            output.print("Step ${stepIndex + 1}: ${step.describe()}")
        } else {
            output.print("Repeat ${stepIndex + 1}: ${step.describe()}")
        }
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<Exception, Any>
    ) {
        val resultMessage = if (result.isLeft()) "FAILED" else "SUCCESS"
        output.println(" - $resultMessage")
    }
}