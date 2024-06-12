package com.github.aivanovski.picoautomator.domain.newapi.entity

import kotlinx.serialization.Serializable

@Serializable
data class UiElementSelector private constructor(
    val id: String? = null,
    val text: String? = null,
    val containsText: String? = null,
    val contentDescription: String? = null,
    val isFocused: Boolean? = null,
    val isClickable: Boolean? = null,
    val isLongClickable: Boolean? = null,
    val isIgnoreTextCase: Boolean = true, // TODO: rename
    val type: SelectionType
) {

    enum class SelectionType {
        ID,
        TEXT,
        CONTENT_DESCRIPTION,
        CONTAINS_TEXT,
        FOCUSED,
        CLICKABLE,
        LONG_CLICKABLE
    }

    companion object {

        fun id(id: String) = UiElementSelector(
            id = id,
            type = SelectionType.ID
        )

        fun text(text: String) = UiElementSelector(
            text = text,
            type = SelectionType.TEXT
        )

        fun containsText(text: String) = UiElementSelector(
            containsText = text,
            type = SelectionType.CONTAINS_TEXT
        )

        fun contentDescription(contentDescription: String) = UiElementSelector(
            contentDescription = contentDescription,
            type = SelectionType.CONTENT_DESCRIPTION
        )

        fun isFocused(isFocused: Boolean) = UiElementSelector(
            isFocused = isFocused,
            type = SelectionType.FOCUSED
        )

        fun isClickable(isClickable: Boolean) = UiElementSelector(
            isClickable = isClickable,
            type = SelectionType.CLICKABLE
        )

        fun isLongClickable(isLongClickable: Boolean) = UiElementSelector(
            isLongClickable = isLongClickable,
            type = SelectionType.LONG_CLICKABLE
        )
    }
}