package com.github.aivanovski.picoautomator.android.ui.data.api

import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.domain.entity.Either

interface FlowApi {
    fun getFirstStep(): Either<Exception, FlowStep>
    fun getNextStep(): Either<Exception, FlowStep?>
    fun onStepComplete(step: FlowStep, isSuccess: Boolean): Either<Exception, StepAction>
}