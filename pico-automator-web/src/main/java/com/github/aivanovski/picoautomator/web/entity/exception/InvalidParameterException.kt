package com.github.aivanovski.picoautomator.web.entity.exception

import com.github.aivanovski.picoautomator.web.presentation.Errors.INVALID_PARAMETER

class InvalidParameterException(
    parameterName: String
) : AppException(
    message = INVALID_PARAMETER.format(parameterName)
)