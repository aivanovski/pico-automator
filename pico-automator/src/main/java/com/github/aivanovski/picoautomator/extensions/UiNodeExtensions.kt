package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiNode
import java.util.LinkedList

fun UiNode.matches(element: ElementReference): Boolean {
    return when (element) {
        is ElementReference.Id -> this.resourceId == "$packageName:id/${element.id}"
        is ElementReference.Text -> this.text == element.text
    }
}

fun UiNode.findNode(predicate: (UiNode) -> Boolean): UiNode? {
    for (node in this.nodes) {
        val matched = node.traverse(predicate).firstOrNull()
        if (matched != null) {
            return matched
        }
    }
    return null
}

fun UiNode.traverse(predicate: (UiNode) -> Boolean): List<UiNode> {
    val result = mutableListOf<UiNode>()

    val nodes = LinkedList<UiNode>()
    nodes.addAll(this.nodes)

    while (nodes.isNotEmpty()) {
        repeat(nodes.size) {
            val node = nodes.removeFirst()

            if (predicate(node)) {
                result.add(node)
            }

            nodes.addAll(node.nodes)
        }
    }

    return result
}