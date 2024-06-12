package com.github.aivanovski.picoautomator.android.entity.exception

import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException

class FlowException(
    cause: FlowExecutionException
) : AppException(cause = cause)