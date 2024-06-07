package com.github.aivanovski.picoautomator.web.domain.flow

import com.github.aivanovski.picoautomator.webapi.TestStep
import com.github.aivanovski.picoautomator.webapi.UiElement

object FlowDsl {

    fun steps(
        packageName: String,
        uid: String,
        content: FlowBuilder.() -> Unit
    ): List<TestStep> {
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

        private val steps = mutableListOf<TestStep>()
        private var stepCounter = 0

        fun sendBroadcast(
            action: String,
            data: Map<String, String>
        ) {
            steps.add(
                TestStep.SendBroadcast(
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
                TestStep.Launch(
                    stepUid = "$baseUid/$stepCounter",
                    packageName = packageName
                )
            )
            stepCounter++
        }

        fun assertVisible(
            vararg elements: UiElement
        ) {
            steps.add(
                TestStep.AssertVisible(
                    stepUid = "$baseUid/$stepCounter",
                    elements = elements.toList()
                )
            )
            stepCounter++
        }

        fun assertNotVisible(
            vararg elements: UiElement
        ) {
            steps.add(
                TestStep.AssertVisible(
                    stepUid = "$baseUid/$stepCounter",
                    elements = elements.toList()
                )
            )
            stepCounter++
        }

        fun inputText(
            text: String,
            element: UiElement? = null
        ) {
            steps.add(
                TestStep.InputText(
                    stepUid = "$baseUid/$stepCounter",
                    text = text,
                    element = element
                )
            )
            stepCounter++
        }

        fun tapOn(
            element: UiElement
        ) {
            steps.add(
                TestStep.TapOn(
                    stepUid = "$baseUid/$stepCounter",
                    element = element
                )
            )
            stepCounter++
        }

        fun build(): List<TestStep> {
            return steps
        }
    }
}