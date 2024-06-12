package com.github.aivanovski.picoautomator.domain.newapi.entity.exception

import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

open class NodeException(
    message: String
) : FlowExecutionException(message)

class FailedToFindNodeException(
    selector: UiElementSelector
) : NodeException(
    message = "Failed to find node: %s".format(selector.toReadableFormat())
)