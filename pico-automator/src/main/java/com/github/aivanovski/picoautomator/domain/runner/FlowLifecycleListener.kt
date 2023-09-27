package com.github.aivanovski.picoautomator.domain.runner

import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

interface FlowLifecycleListener {

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
        result: Either<Exception, Any>
    ) {
    }

    fun onStepStarted(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        repeatCount: Int
    ) {
    }

    fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<Exception, Any>
    ) {
    }
}