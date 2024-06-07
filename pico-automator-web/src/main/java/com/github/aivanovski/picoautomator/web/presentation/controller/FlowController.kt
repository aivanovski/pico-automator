package com.github.aivanovski.picoautomator.web.presentation.controller

import com.github.aivanovski.picoautomator.data.resources.ResourceProvider
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.data.repository.FlowRepository
import com.github.aivanovski.picoautomator.web.entity.ErrorResponse
import com.github.aivanovski.picoautomator.web.entity.Flow
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.EntityNotFoundException
import com.github.aivanovski.picoautomator.web.entity.exception.InvalidParameterException
import com.github.aivanovski.picoautomator.web.extensions.toErrorResponse
import com.github.aivanovski.picoautomator.web.presentation.routes.Api.ID
import com.github.aivanovski.picoautomator.webapi.FlowItemDto
import com.github.aivanovski.picoautomator.webapi.FlowsItemDto
import com.github.aivanovski.picoautomator.webapi.response.FlowResponse
import com.github.aivanovski.picoautomator.webapi.response.FlowsResponse
import io.ktor.http.HttpStatusCode
import java.util.Base64

class FlowController(
    private val flowRepository: FlowRepository,
    private val resourceProvider: ResourceProvider
) {

    fun getFlow(
        user: User,
        flowUid: String
    ): Either<ErrorResponse, FlowResponse> {
        if (flowUid.isEmpty()) {
            return newInvalidParameterResponse(ID)
        }

        val getFlowsResult = flowRepository.getFlows(user)
        if (getFlowsResult.isLeft()) {
            return getFlowsResult.toErrorResponse()
        }

        val flows = getFlowsResult.unwrap()
            .filter { flow -> flow.uid == flowUid }
        if (flows.isEmpty()) {
            return newFlowNotFoundResponse(flowUid)
        }

        val flow = flows.first()

        val getContent = resourceProvider.read(flow.resource)
        if (getContent.isLeft()) {
            return getContent.toErrorResponse()
        }

        val flowBytes = getContent.unwrap().toByteArray()
        val content = Base64.getEncoder().encode(flowBytes)
            .let { bytes -> String(bytes) }

        return Either.Right(
            FlowResponse(
                FlowItemDto(
                    uid = flow.uid,
                    projectUid = flow.projectUid,
                    name = flow.name,
                    base64Content = content
                )
            )
        )
    }

    fun getFlows(
        user: User
    ): Either<ErrorResponse, FlowsResponse> {
        val getFlowsResult = flowRepository.getFlows(user)
        if (getFlowsResult.isLeft()) {
            return getFlowsResult.toErrorResponse()
        }

        val flows = getFlowsResult.unwrap().map { item ->
            FlowsItemDto(
                uid = item.uid,
                projectUid = item.projectUid,
                name = item.name
            )
        }

        return Either.Right(FlowsResponse(flows))
    }

    private fun newInvalidParameterResponse(
        name: String
    ): Either.Left<ErrorResponse> {
        return Either.Left(
            ErrorResponse.fromException(
                status = HttpStatusCode.BadRequest,
                exception = InvalidParameterException(name)
            )
        )
    }

    private fun newFlowNotFoundResponse(
        uid: String
    ): Either.Left<ErrorResponse> {
        return Either.Left(
            ErrorResponse.fromException(
                status = HttpStatusCode.NotFound,
                exception = EntityNotFoundException(
                    Flow::class.java.simpleName,
                    "uid",
                    uid
                )
            )
        )
    }
}