package com.github.aivanovski.picoautomator.domain.entity

sealed class ElementReference {

    data class Id(val id: String) : ElementReference() {
        override fun toString(): String {
            return "id = $id"
        }
    }

    data class Text(val text: String) : ElementReference() {
        override fun toString(): String {
            return "text = $text"
        }
    }

    data class ContainsText(
        val text: String,
        val ignoreCase: Boolean = true
    ) : ElementReference() {
        override fun toString(): String {
            return "has text = $text"
        }
    }

    companion object {
        fun id(id: String): Id = Id(id)
        fun text(text: String): Text = Text(text)
        fun containsText(text: String) = ContainsText(text)
    }
}