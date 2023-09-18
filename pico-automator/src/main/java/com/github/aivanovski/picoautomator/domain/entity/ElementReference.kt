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

    companion object {
        fun id(id: String): Id = Id(id)
        fun text(text: String): Text = Text(text)
    }
}