package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.web.entity.Flow
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either

interface FlowRepository {
    fun getFlows(user: User): Either<AppException, List<Flow>>
}