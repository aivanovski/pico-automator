package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.domain.steps.AssertVisible
import com.github.aivanovski.picoautomator.domain.steps.InputText
import com.github.aivanovski.picoautomator.domain.steps.Launch
import com.github.aivanovski.picoautomator.domain.steps.Tap
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

class FlowBuilder private constructor(
    private val name: String,
    private val packageName: String,
    private val predecessors: List<Flow> = emptyList()
) {

    private var isAddLaunchCommands = true
    private val steps = mutableListOf<FlowStep>()

    fun tapOn(element: ElementReference): FlowBuilder {
        steps.add(Tap(element = element))
        return this
    }

    fun inputText(text: String): FlowBuilder {
        steps.add(InputText(text = text))
        return this
    }

    fun assertVisible(vararg element: ElementReference): FlowBuilder {
        steps.add(
            AssertVisible(
                parentElement = null,
                elements = element.toList()
            )
        )
        return this
    }

    fun assertVisibleInside(
        parent: ElementReference,
        elements: List<ElementReference>
    ): FlowBuilder {
        if (elements.isEmpty()) {
            throw IllegalArgumentException("Elements must not be empty")
        }

        steps.add(
            AssertVisible(
                parentElement = parent,
                elements = elements
            )
        )

        return this
    }

    fun noLaunch(): FlowBuilder {
        isAddLaunchCommands = false
        return this
    }

    fun build(): Flow {
        return Flow(
            name = name,
            packageName = packageName,
            predecessors = predecessors,
            steps = mutableListOf<FlowStep>().apply {
                if (predecessors.isEmpty() && isAddLaunchCommands) {
                    add(Launch(packageName))
                }

                addAll(steps)
            }
        )
    }

    companion object {

        fun newFlow(
            name: String,
            packageName: String
        ): FlowBuilder =
            FlowBuilder(
                name = name,
                packageName = packageName
            )

        fun newFlowAfter(
            name: String,
            vararg predecessors: Flow
        ): FlowBuilder =
            FlowBuilder(
                name = name,
                packageName = predecessors.first().packageName,
                predecessors = predecessors.toList()
            )
    }
}