package com.github.aivanovski.picoautomator.web.presentation.controller

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.data.repository.ProjectRepository
import com.github.aivanovski.picoautomator.web.entity.ErrorResponse
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.extensions.toErrorResponse
import com.github.aivanovski.picoautomator.webapi.response.ProjectsItemDto
import com.github.aivanovski.picoautomator.webapi.response.ProjectsResponse

class ProjectController(
    private val projectRepository: ProjectRepository
) {

    fun getProjects(
        user: User
    ): Either<ErrorResponse, ProjectsResponse> {
        val getProjects = projectRepository.getProjects()
        if (getProjects.isLeft()) {
            return getProjects.toErrorResponse()
        }

        val projects = getProjects.unwrap()
            .map { project ->
                ProjectsItemDto(
                    uid = project.uid,
                    name = project.name
                )
            }

        return Either.Right(ProjectsResponse(projects))
    }
}