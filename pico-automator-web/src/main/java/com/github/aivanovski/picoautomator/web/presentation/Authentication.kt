package com.github.aivanovski.picoautomator.web.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.aivanovski.picoautomator.web.entity.JwtData
import com.github.aivanovski.picoautomator.web.presentation.Errors.INVALID_OR_EXPIRED_TOKEN
import com.github.aivanovski.picoautomator.webapi.response.ErrorMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

const val AUTH_PROVIDER = "jwt-auth-provider"

private val logger = LoggerFactory.getLogger("Authentication")

fun Application.configureAuthentication() {
    val jwtData = JwtData.DEFAULT // TODO: read from properties

    install(Authentication) {
        jwt(AUTH_PROVIDER) {
            realm = jwtData.realm

            verifier(
                JWT.require(Algorithm.HMAC256(jwtData.secret))
                    .withAudience(jwtData.audience)
                    .withIssuer(jwtData.issuer)
                    .build()
            )

            validate { credential ->
                JWTPrincipal(credential.payload)
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = ErrorMessage(INVALID_OR_EXPIRED_TOKEN)
                )
            }
        }
    }
}