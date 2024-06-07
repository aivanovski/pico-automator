package com.github.aivanovski.picoautomator.webapi

import kotlinx.serialization.Serializable

@Serializable
sealed interface UiElement {

    @Serializable
    data class Id(val id: String) : UiElement

    @Serializable
    data class Text(val text: String) : UiElement

    @Serializable
    data class ContainsText(
        val text: String,
        val ignoreCase: Boolean = true
    ) : UiElement

    @Serializable
    data class ContentDescription(
        val contentDescription: String
    ) : UiElement
}