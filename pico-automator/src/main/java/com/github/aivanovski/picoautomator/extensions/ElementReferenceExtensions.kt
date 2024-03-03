package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.entity.ElementReference

fun ElementReference.toReadableFormat(): String {
    return when (this) {
        is ElementReference.Id -> "[id = $id]"
        is ElementReference.Text -> "[text = $text]"
        is ElementReference.ContainsText -> "[has text = $text]"
        is ElementReference.ContentDescription -> "[desc = $contentDescription]"
    }
}

fun List<ElementReference>.toReadableFormat(): String {
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