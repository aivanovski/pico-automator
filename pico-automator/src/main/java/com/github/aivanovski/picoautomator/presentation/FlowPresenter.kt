package com.github.aivanovski.picoautomator.presentation

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowRunnerCallbacks
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

class FlowPresenter(
    private val output: OutputWriter
) : FlowRunnerCallbacks {

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

    override fun onFlowFinished(flow: Flow, result: Either<Exception, Unit>) {
        if (result.isRight()) {
            output.println("Finished successfully")
        } else {
            val exception = result.unwrapError()
            output.println("Failed: ${exception.message ?: exception.javaClass.simpleName}")
            output.printStackTrace(exception)
        }
    }

    override fun onStepStarted(flow: Flow, step: FlowStep, repeatCount: Int) {
        val stepIndex = flow.steps.indexOf(step) + 1
        val stepCount = flow.steps.size

        if (repeatCount == 0) {
            output.print("Step $stepIndex/$stepCount: ${step.describe()}")
        } else {
            output.print("Repeat $stepIndex/$stepCount: ${step.describe()}")
        }
    }

    override fun onStepFinished(flow: Flow, step: FlowStep, result: Either<Exception, Unit>) {
        val resultMessage = if (result.isLeft()) "FAILED" else "SUCCESS"
        output.println(" - $resultMessage")
    }
}