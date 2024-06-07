package com.github.aivanovski.picoautomator.webapi

import kotlinx.serialization.Serializable

@Serializable
sealed interface TestStep {

    @Serializable
    data class Launch(
        val stepUid: String,
        val packageName: String
    ) : TestStep

    @Serializable
    data class TapOn(
        val stepUid: String,
        val element: UiElement
    ) : TestStep

    @Serializable
    data class AssertVisible(
        val stepUid: String,
        val elements: List<UiElement>
    ) : TestStep

    @Serializable
    data class AssertNotVisible(
        val stepUid: String,
        val elements: List<UiElement>
    ) : TestStep

    @Serializable
    data class InputText(
        val stepUid: String,
        val text: String,
        val element: UiElement?
    ) : TestStep

    @Serializable
    data class SendBroadcast(
        val stepUid: String,
        val packageName: String,
        val action: String,
        val data: Map<String, String> = emptyMap()
    ) : TestStep
}