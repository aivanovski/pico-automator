package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

interface ExecutableFlowStep<out T : Any> : FlowStep {
    fun execute(driver: Driver): Either<Exception, T>
}