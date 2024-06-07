package com.github.aivanovski.picoautomator.web.entity.exception

import com.github.aivanovski.picoautomator.web.presentation.Errors.ENTITY_NOT_FOUND

class EntityNotFoundException(
    entity: String,
    key: String,
    value: String
) : AppException(ENTITY_NOT_FOUND.format(entity, key, value))