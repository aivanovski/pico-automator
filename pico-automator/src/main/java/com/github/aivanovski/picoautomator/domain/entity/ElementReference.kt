package com.github.aivanovski.picoautomator.domain.entity

import kotlinx.serialization.Serializable

@Serializable
sealed class ElementReference {

    @Serializable
    data class Id(val id: String) : ElementReference()

    @Serializable
    data class Text(val text: String) : ElementReference()

    @Serializable
    data class ContainsText(
        val text: String,
        val ignoreCase: Boolean = true
    ) : ElementReference()

    @Serializable
    data class ContentDescription(
        val contentDescription: String
    ) : ElementReference()

    companion object {

        @JvmStatic
        fun id(id: String): Id = Id(id)

        @JvmStatic
        fun text(text: String): Text = Text(text)

        @JvmStatic
        fun containsText(text: String): ContainsText = ContainsText(text)

        @JvmStatic
        fun contentDesc(desc: String): ContentDescription = ContentDescription(desc)
    }
}