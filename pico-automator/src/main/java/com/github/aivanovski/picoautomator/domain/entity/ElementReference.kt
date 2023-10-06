package com.github.aivanovski.picoautomator.domain.entity

sealed class ElementReference {

    data class Id(val id: String) : ElementReference()

    data class Text(val text: String) : ElementReference()

    data class ContainsText(
        val text: String,
        val ignoreCase: Boolean = true
    ) : ElementReference()

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