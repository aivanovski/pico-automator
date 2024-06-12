package com.github.aivanovski.picoautomator.android.domain.flow

import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.steps.StepCommand

interface FlowLifecycleListener {

    fun onFlowStarted(
        flow: FlowEntry
    )

    fun onFlowFinished(
        flow: FlowEntry,
        result: Either<AppException, Any>
    )

    fun onStepStarted(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    )

    fun onStepFinished(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        result: Either<AppException, Any>
    )
}