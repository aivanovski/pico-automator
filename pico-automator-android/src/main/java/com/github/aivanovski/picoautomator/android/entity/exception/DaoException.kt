package com.github.aivanovski.picoautomator.android.entity.exception

open class DaoException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class FailedToFindEntityException(
    entityName: String,
    entityField: String,
    fieldValue: String
) : DaoException(
    message = "Unable to find entity %s: %s=%s".format(entityName, entityField, fieldValue),
    cause = null
)