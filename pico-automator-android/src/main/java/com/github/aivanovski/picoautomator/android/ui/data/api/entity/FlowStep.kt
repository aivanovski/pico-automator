package com.github.aivanovski.picoautomator.android.ui.data.api.entity

import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import kotlinx.serialization.Serializable

@Serializable
sealed interface FlowStep {

    val uid: String
        get() = when (this) {
            is Launch -> stepUid
            is TapOn -> stepUid
            is AssertVisible -> stepUid
            is AssertNotVisible -> stepUid
            is InputText -> stepUid
            is SendBroadcast -> stepUid
        }

    @Serializable
    data class Launch(
        val stepUid: String,
        val packageName: String
    ) : FlowStep

    @Serializable
    data class TapOn(
        val stepUid: String,
        val element: ElementReference
    ) : FlowStep

    @Serializable
    data class AssertVisible(
        val stepUid: String,
        val elements: List<ElementReference>
    ) : FlowStep

    @Serializable
    data class AssertNotVisible(
        val stepUid: String,
        val elements: List<ElementReference>
    ) : FlowStep

    @Serializable
    data class InputText(
        val stepUid: String,
        val text: String,
        val element: ElementReference?
    ) : FlowStep

    @Serializable
    data class SendBroadcast(
        val stepUid: String,
        val packageName: String,
        val action: String,
        val data: Map<String, String> = emptyMap()
    ) : FlowStep
}