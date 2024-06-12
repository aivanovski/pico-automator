package com.github.aivanovski.picoautomator.domain.newapi.entity

import com.github.aivanovski.picoautomator.domain.entity.Duration
import kotlinx.serialization.Serializable

@Serializable
sealed interface FlowStep {

    @Serializable
    data class Launch(
        val packageName: String
    ) : FlowStep

    @Serializable
    data class SendBroadcast(
        val packageName: String,
        val action: String,
        val data: Map<String, String>
    ) : FlowStep

    @Serializable
    data class TapOn(
        val element: UiElementSelector,
        val isLong: Boolean = false
    ) : FlowStep

    @Serializable
    data class AssertVisible(
        val elements: List<UiElementSelector>
    ) : FlowStep

    @Serializable
    data class AssertNotVisible(
        val elements: List<UiElementSelector>
    ) : FlowStep

    @Serializable
    data class InputText(
        val text: String,
        val element: UiElementSelector?
    ) : FlowStep

    @Serializable
    data class PressKey(
        val key: KeyCode
    ) : FlowStep

    @Serializable
    data class WaitUntil(
        val element: UiElementSelector,
        val step: Duration,
        val timeout: Duration
    ) : FlowStep

    @Serializable
    data class RunFlow(
        val flowUid: String
    ) : FlowStep
}