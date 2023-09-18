package com.github.aivanovski.picoautomator.domain.runner

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

interface FlowRunnerCallbacks {

    fun onDeviceSelected(
        device: Device
    ) {
    }

    fun onFlowStarted(
        flow: Flow,
        isPredecessor: Boolean
    ) {
    }

    fun onFlowFinished(
        flow: Flow,
        result: Either<Exception, Unit>
    ) {
    }

    fun onStepStarted(
        flow: Flow,
        step: FlowStep,
        repeatCount: Int
    ) {
    }

    fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        result: Either<Exception, Unit>
    ) {
    }
}