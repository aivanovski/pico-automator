package com.github.aivanovski.picoautomator.domain.entity.exception

class CompleteExecutionException(
    val completionMessage: String
) : ExecutionException(completionMessage)