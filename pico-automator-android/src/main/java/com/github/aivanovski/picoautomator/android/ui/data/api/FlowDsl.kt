package com.github.aivanovski.picoautomator.android.ui.data.api

import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep
import com.github.aivanovski.picoautomator.domain.entity.ElementReference

object FlowDsl {

    fun createFlow(
        packageName: String,
        uid: String,
        content: FlowBuilder.() -> Unit
    ): List<FlowStep> {
        return FlowBuilder(packageName, uid)
            .apply {
                content.invoke(this)
            }
            .build()
    }

    class FlowBuilder(
        private val packageName: String,
        private val baseUid: String
    ) {

        private val steps = mutableListOf<FlowStep>()
        private var stepCounter = 0

        fun sendBroadcast(
            action: String,
            data: Map<String, String>
        ) {
            steps.add(
                FlowStep.SendBroadcast(
                    stepUid = "$baseUid/$stepCounter",
                    packageName = packageName,
                    action = action,
                    data = data
                )
            )
            stepCounter++
        }

        fun launch() {
            steps.add(
                FlowStep.Launch(
                    stepUid = "$baseUid/$stepCounter",
                    packageName = packageName
                )
            )
            stepCounter++
        }

        fun assertVisible(
            vararg elements: ElementReference
        ) {
            steps.add(
                FlowStep.AssertVisible(
                    stepUid = "$baseUid/$stepCounter",
                    elements = elements.toList()
                )
            )
            stepCounter++
        }

        fun assertNotVisible(
            vararg elements: ElementReference
        ) {
            steps.add(
                FlowStep.AssertVisible(
                    stepUid = "$baseUid/$stepCounter",
                    elements = elements.toList()
                )
            )
            stepCounter++
        }

        fun inputText(
            text: String,
            element: ElementReference? = null
        ) {
            steps.add(
                FlowStep.InputText(
                    stepUid = "$baseUid/$stepCounter",
                    text = text,
                    element = element
                )
            )
            stepCounter++
        }

        fun tapOn(
            element: ElementReference
        ) {
            steps.add(
                FlowStep.TapOn(
                    stepUid = "$baseUid/$stepCounter",
                    element = element
                )
            )
            stepCounter++
        }

        fun build(): List<FlowStep> {
            return steps
        }
    }
}