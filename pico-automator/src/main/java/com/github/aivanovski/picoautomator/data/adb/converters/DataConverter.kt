package com.github.aivanovski.picoautomator.data.adb.converters

import com.github.aivanovski.picoautomator.data.adb.entity.UiNodeEntity
import com.github.aivanovski.picoautomator.domain.entity.Bounds
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import com.github.aivanovski.picoautomator.util.isDigitOnly
import java.util.LinkedList

internal fun UiNodeEntity.convertToUiNode(): UiTreeNode {
    val next = LinkedList<Pair<UiTreeNode?, UiNodeEntity>>()
    next.add(Pair(null, this))

    var root: UiTreeNode? = null
    while (next.isNotEmpty()) {
        val (parent, node) = next.removeFirst()

        val newNode = UiTreeNode(
            resourceId = node.resourceId,
            text = node.text,
            className = node.className,
            packageName = node.packageName,
            bounds = node.bounds?.let { parseBounds(it) },
            isFocused = node.isFocused,
            nodes = mutableListOf()
        )

        parent?.nodes?.add(newNode)
        if (root == null) {
            root = newNode
        }

        for (childNode in node.nodes) {
            next.add(Pair(newNode, childNode))
        }
    }

    return root ?: throw IllegalStateException()
}

private fun parseBounds(text: String): Bounds? {
    val values = text.replace("[", "")
        .replace("]", ",")
        .split(",")
        .filter { it.isNotBlank() && it.isDigitOnly() }
        .map { it.toInt() }

    if (values.size != 4) {
        return null
    }

    return Bounds(
        left = values[0],
        top = values[1],
        right = values[2],
        bottom = values[3]
    )
}