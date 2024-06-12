package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector.SelectionType

fun UiElementSelector.toReadableFormat(): String {
    return when (type) {
        SelectionType.ID -> "[id = $id]"
        SelectionType.TEXT -> "[text = $text]"
        SelectionType.CONTAINS_TEXT -> "[has text = $text, ignoreCase = $isIgnoreTextCase]"
        SelectionType.CONTENT_DESCRIPTION -> "[content descriptor = $contentDescription]"
        SelectionType.FOCUSED -> "[is in focus = $isFocused]"
        SelectionType.CLICKABLE -> "[is clickable = $isClickable]"
        SelectionType.LONG_CLICKABLE -> "[is long clickable = $isLongClickable]"
    }
}

fun List<UiElementSelector>.toReadableFormat(): String {
    return if (this.size == 1) {
        this.first().toReadableFormat()
    } else {
        this.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            transform = { element -> element.toReadableFormat() }
        )
    }
}