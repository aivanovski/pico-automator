package com.github.aivanovski.picoautomator.cli

import com.github.aivanovski.picoautomator.PicoAutomatorDsl.newFlow
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.millis
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.seconds
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.containsText
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.id
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.text
import com.github.aivanovski.picoautomator.domain.runner.FlowRunner
import com.github.aivanovski.picoautomator.extensions.hasElement
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputWriter

fun main(args: Array<String>) {
    val flow = newFlow("Brute force password") {
        fun formatPassword(index: Int): String {
            return when {
                index < 10 -> "abc00$index"
                index < 100 -> "abc0$index"
                else -> "abc$index"
            }
        }

        val passwordRange = 122..125

        for (idx in passwordRange) {
            val password = formatPassword(idx)

            launch("com.ivanovsky.passnotes")
            waitFor(text("automation.kdbx"), timeout = seconds(5), step = millis(500))

            tapOn(id("password"))
            inputText(password)
            tapOn(id("unlockButton"))

            for (j in 1..10) {
                wait(millis(500))

                val tree = getUiTree()

                when {
                    tree.hasElement(text("Groups")) -> {
                        complete("password = $password")
                    }

                    tree.hasElement(containsText("Wrong key")) -> {
                        break
                    }
                }
            }
        }

        fail("Unable to brute force password")
    }

    FlowRunner()
        .apply {
            addLifecycleListener(StandardOutputFlowReporter(StandardOutputWriter()))
        }
        .run(flow)
}