package com.github.aivanovski.picoautomator.android.extensions

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.android.entity.UiEntity
import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.domain.entity.Bounds
import java.util.LinkedList

typealias AccessibilityUiNode = UiNode<AccessibilityNodeInfo>

fun AccessibilityNodeInfo.convertToUiNode(): AccessibilityUiNode {
    val queue = LinkedList<Pair<AccessibilityUiNode?, AccessibilityNodeInfo>>()
    queue.add(null to this)

    var result: AccessibilityUiNode? = null
    val bounds = Rect()

    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val (parent, node) = queue.removeFirst()

            node.getBoundsInScreen(bounds)

            val uiEntity = node.toUiEntity(
                bounds = bounds.toBounds()
            )
            val uiNode = UiNode(
                source = node,
                entity = uiEntity,
                nodes = mutableListOf()
            )

            if (parent != null) {
                parent.nodes.add(uiNode)
            } else {
                result = uiNode
            }

            val childNodes = node.getNodes()
            for (childNode in childNodes) {
                queue.add(uiNode to childNode)
            }
        }
    }

    return result ?: throw IllegalStateException()
}

fun AccessibilityNodeInfo.getNodes(): Iterable<AccessibilityNodeInfo> {
    val nodes = mutableListOf<AccessibilityNodeInfo>()

    for (idx in 0 until childCount) {
        val child = getChild(idx) ?: continue
        nodes.add(child)
    }

    return nodes
}

fun AccessibilityNodeInfo.toUiEntity(
    bounds: Bounds? = null
): UiEntity {
    return UiEntity(
        resourceId = viewIdResourceName,
        packageName = packageName?.toString(),
        className = className?.toString(),
        bounds = bounds,
        text = text?.toString(),
        contentDescription = contentDescription?.toString(),
        isEnabled = isEnabled,
        isFocused = isFocused,
        isFocusable = isFocusable,
        isClickable = isClickable,
        isLongClickable = isLongClickable,
        isCheckable = isCheckable,
        isChecked = isChecked
    )
}