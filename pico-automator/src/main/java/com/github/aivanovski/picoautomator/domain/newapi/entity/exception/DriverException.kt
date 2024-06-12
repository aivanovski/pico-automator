package com.github.aivanovski.picoautomator.domain.newapi.entity.exception

open class DriverException(
    message: String? = null,
    cause: Exception? = null
) : FlowExecutionException(message, cause)

class FailedToGetUiNodesException : DriverException(
    message = "Failed to get UI nodes"
)

class FailedToPerformActionException(
    actionName: String
) : DriverException(
    message = "Unable to perform action: $actionName"
)