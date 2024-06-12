package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps
import com.github.aivanovski.picoautomator.domain.steps.StepCommand

class RunFlow(
    val flow: FlowWithSteps,
    private val commands: List<StepCommand>
) : CompositeStepCommand {

    override fun describe(): String {
        return "Run flow '%s'".format(flow.entry.name)
    }

    override fun getCommands(): List<StepCommand> {
        return commands
    }
}