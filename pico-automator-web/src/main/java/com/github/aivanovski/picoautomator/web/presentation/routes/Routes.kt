package com.github.aivanovski.picoautomator.web.presentation.routes

import com.github.aivanovski.picoautomator.web.di.GlobalInjector.get
import com.github.aivanovski.picoautomator.web.domain.service.AuthService
import com.github.aivanovski.picoautomator.web.entity.ErrorResponse
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.picoautomator.web.presentation.Errors.ERROR_HAS_BEEN_OCCURRED
import com.github.aivanovski.picoautomator.web.presentation.Errors.INVALID_OR_EXPIRED_TOKEN
import com.github.aivanovski.picoautomator.web.presentation.controller.FlowController
import com.github.aivanovski.picoautomator.web.presentation.controller.LoginController
import com.github.aivanovski.picoautomator.web.presentation.routes.Api.FLOW
import com.github.aivanovski.picoautomator.web.presentation.routes.Api.ID
import com.github.aivanovski.picoautomator.web.presentation.routes.Api.LOGIN
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.presentation.controller.ProjectController
import com.github.aivanovski.picoautomator.web.presentation.routes.Api.PROJECT
import com.github.aivanovski.picoautomator.web.utils.StringUtils
import com.github.aivanovski.picoautomator.webapi.response.ErrorMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("Routes")

fun Application.configureRouting() {
    val authService = get<AuthService>()
    val loginController = get<LoginController>()
    val flowController = get<FlowController>()
    val projectController = get<ProjectController>()

    routing {
        post(LOGIN) {
            handle(call) {
                loginController.login(call.receive())
            }
        }

        authenticate(AUTH_PROVIDER) {
            get(FLOW) {
                handleAuthenticated(authService, call) { user ->
                    flowController.getFlows(user)
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("$FLOW/{$ID}") {
                handleAuthenticated(authService, call) { user ->
                    val uid = call.parameters[ID] ?: StringUtils.EMPTY
                    flowController.getFlow(user, uid)
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get(PROJECT) {
                handleAuthenticated(authService, call) { user ->
                    projectController.getProjects(user)
                }
            }
        }
    }
}

suspend inline fun <reified T : Any> handleAuthenticated(
    authService: AuthService,
    call: ApplicationCall,
    block: (user: User) -> Either<ErrorResponse, T>
) {
    val principal = call.principal<JWTPrincipal>()
    if (principal == null) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = ErrorMessage(message = INVALID_OR_EXPIRED_TOKEN)
        )
        return
    }

    val isValidTokenResult = authService.validateToken(principal)
    if (isValidTokenResult.isLeft()) {
        call.sendResponse(isValidTokenResult.unwrapError())
        return
    }

    val user = isValidTokenResult.unwrap()

    val response = block.invoke(user)
    call.sendResponse(response)
}

suspend inline fun <reified T : Any> handle(
    call: ApplicationCall,
    block: () -> Either<ErrorResponse, T>
) {
    val response = block.invoke()
    call.sendResponse(response)
}

suspend inline fun <reified T : Any> ApplicationCall.sendResponse(
    response: Either<ErrorResponse, T>
) {
    logger.debug("Request: {} isSuccess={}", request.uri, response.isRight())

    if (response.isRight()) {
        respond(
            status = HttpStatusCode.OK,
            message = response.unwrap()
        )
    } else {
        sendResponse(response.unwrapError())
    }
}

suspend fun ApplicationCall.sendResponse(error: ErrorResponse) {
    logger.error("Response error: status={}, message={}", error.status, error.message)

    error.exception.printStackTrace()

    respond(
        status = error.status,
        message = error.toErrorMessage()
    )
}

private fun ErrorResponse.toErrorMessage(): ErrorMessage {
    return ErrorMessage(
        message = message ?: ERROR_HAS_BEEN_OCCURRED
    )
}