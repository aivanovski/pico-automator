package com.github.aivanovski.picoautomator.cli

import com.github.aivanovski.picoautomator.PicoAutomatorDsl.newFlow
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.id
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.text
import com.github.aivanovski.picoautomator.domain.runner.FlowRunner
import com.github.aivanovski.picoautomator.presentation.StandardOutputFlowReporter
import com.github.aivanovski.picoautomator.presentation.StandardOutputWriter

fun main(args: Array<String>) {
    val flow = newFlow("Login") {
        launch("org.wikipedia")
        tapOn(text("Search"))
        tapOn(text("Search Wikipedia"))
        inputText(text("Search Wikipedia"), "Dunning")
        tapOn(text("Dunning–Kruger effect"))
        assertVisible(id("page_web_view"))
    }

    FlowRunner()
        .apply {
            addLifecycleListener(StandardOutputFlowReporter(StandardOutputWriter()))
        }
        .run(flow)
}