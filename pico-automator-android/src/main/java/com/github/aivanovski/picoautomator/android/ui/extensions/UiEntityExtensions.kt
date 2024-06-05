package com.github.aivanovski.picoautomator.android.ui.extensions

import com.github.aivanovski.picoautomator.android.ui.domain.entity.UiEntity
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import java.lang.StringBuilder

fun UiEntity.matches(element: ElementReference): Boolean {
    return when (element) {
        is ElementReference.Id -> this.resourceId == "$packageName:id/${element.id}"
        is ElementReference.Text -> this.text == element.text
        is ElementReference.ContainsText -> {
            this.text != null && this.text.contains(element.text, ignoreCase = element.ignoreCase)
        }

        is ElementReference.ContentDescription -> {
            this.contentDescription == element.contentDescription
        }
    }
}

