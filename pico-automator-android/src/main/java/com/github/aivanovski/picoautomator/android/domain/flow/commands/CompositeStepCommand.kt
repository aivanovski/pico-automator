package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.domain.steps.StepCommand

interface CompositeStepCommand : StepCommand {
    fun getCommands(): List<StepCommand>
}