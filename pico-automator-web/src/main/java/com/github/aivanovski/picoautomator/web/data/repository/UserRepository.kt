package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either

interface UserRepository {
    fun getUserUid(uid: String): Either<AppException, User>
    fun getUserByName(username: String): Either<AppException, User>
}