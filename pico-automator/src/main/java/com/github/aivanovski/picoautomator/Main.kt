package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.id
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.text
import com.github.aivanovski.picoautomator.domain.runner.FlowRunner
import com.github.aivanovski.picoautomator.presentation.DefaultOutputWriter
import com.github.aivanovski.picoautomator.presentation.FlowPresenter

fun main(args: Array<String>) {
    val loginFlow = FlowBuilder
        .newFlow("Login", "com.ivanovsky.passnotes")
        .assertVisible(text("automation.kdbx"))
        .tapOn(id("password"))
        .inputText("abc123")
        .tapOn(id("unlockButton"))
        .assertVisible(text("Groups"))
        .build()

    val openNoteFlow = FlowBuilder
        .newFlowAfter("Open note", loginFlow)
        .tapOn(text("My Laptop login"))
        .assertVisibleInside(
            parent = id("recyclerView"),
            elements = listOf(
                text("Title"),
                text("My Laptop login"),
                text("UserName"),
                text("john.doe"),
                text("Password"),
                text("Notes"),
                text("Login to my personal Laptop"),
            )
        )
        .build()

    FlowRunner(
        callbacks = FlowPresenter(DefaultOutputWriter())
    ).run(openNoteFlow)
}

