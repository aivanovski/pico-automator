package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.entity.Project
import com.github.aivanovski.picoautomator.web.entity.exception.AppException

interface ProjectRepository {
    fun getProjects(): Either<AppException, List<Project>>
}