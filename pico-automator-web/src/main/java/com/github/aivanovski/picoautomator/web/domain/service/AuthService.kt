package com.github.aivanovski.picoautomator.web.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.github.aivanovski.picoautomator.web.data.repository.UserRepository
import com.github.aivanovski.picoautomator.web.entity.Credentials
import com.github.aivanovski.picoautomator.web.entity.ErrorResponse
import com.github.aivanovski.picoautomator.web.entity.JwtData
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.ExpiredTokenException
import com.github.aivanovski.picoautomator.web.entity.exception.InvalidTokenException
import com.github.aivanovski.picoautomator.domain.entity.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

class AuthService(
    private val userRepository: UserRepository
) {

    private val storage: MutableMap<Credentials, String> = ConcurrentHashMap<Credentials, String>()

    fun isCredentialsValid(credentials: Credentials): Boolean {
        return credentials.username == "admin" &&
            credentials.password == "abc123"
    }

    fun validateToken(principal: JWTPrincipal): Either<ErrorResponse, User> {
        val username = principal.payload.getClaim(USERNAME).asString()
        val expiresAt = principal.expiresAt?.time

        logger.debug(
            "validateToken: username={}, expiresAt={}",
            username,
            expiresAt?.let { Date(it) }
        )

        if (!isValidUsername(username) || expiresAt == null) {
            return Either.Left(
                ErrorResponse.fromException(
                    status = HttpStatusCode.Unauthorized,
                    exception = InvalidTokenException()
                )
            )
        }

        if (System.currentTimeMillis() >= expiresAt) {
            return Either.Left(
                ErrorResponse.fromException(
                    status = HttpStatusCode.Unauthorized,
                    exception = ExpiredTokenException()
                )
            )
        }

        val getUserResult = userRepository.getUserByName(username)
        if (getUserResult.isLeft()) {
            return Either.Left(
                ErrorResponse.fromException(
                    status = HttpStatusCode.Unauthorized,
                    exception = getUserResult.unwrapError()
                )
            )
        }

        val user = getUserResult.unwrap()
        return Either.Right(user)
    }

    fun getOrCreateToken(
        credentials: Credentials
    ): String {
        val existingToken = storage[credentials]
        if (existingToken != null) {
            val token = JWT.decode(existingToken)
            if (!token.isExpired()) {
                logger.debug("Reuse existing token: token={}", existingToken)
                return existingToken
            }
        }

        val newToken = createToken(credentials.username)
        storage[credentials] = newToken

        logger.debug("Created new token: username={}, token={}", credentials, newToken)

        return newToken
    }

    private fun isValidUsername(username: String): Boolean {
        return username == "admin"
    }

    private fun createToken(
        username: String
    ): String {
        val jwtData = JwtData.DEFAULT

        // TODO: expiration was prolonged for developing needs
        // val expires = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        val expires = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)

        return JWT.create()
            .withAudience(jwtData.audience)
            .withIssuer(jwtData.issuer)
            .withClaim(USERNAME, username)
            .withExpiresAt(Date(expires))
            .sign(Algorithm.HMAC256(jwtData.secret))
    }

    private fun DecodedJWT.isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt.time
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)

        private const val USERNAME = "username"
    }
}