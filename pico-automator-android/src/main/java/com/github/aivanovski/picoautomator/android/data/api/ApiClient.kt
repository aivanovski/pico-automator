package com.github.aivanovski.picoautomator.android.data.api

import com.github.aivanovski.picoautomator.android.data.Settings
import com.github.aivanovski.picoautomator.android.data.api.Api.buildGetFlowUrl
import com.github.aivanovski.picoautomator.android.data.api.Api.buildLoginUrl
import com.github.aivanovski.picoautomator.android.data.api.entity.ApiException
import com.github.aivanovski.picoautomator.android.data.api.entity.InvalidHttpStatusCodeException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.webapi.request.LoginRequest
import com.github.aivanovski.picoautomator.webapi.response.FlowResponse
import com.github.aivanovski.picoautomator.webapi.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class ApiClient(
    private val httpClient: HttpClient,
    private val settings: Settings
) {

    suspend fun getFlow(flowUid: String): Either<ApiException, FlowResponse> {
        val response = get(buildGetFlowUrl(flowUid))
        return if (response.isRight()) {
            parseJson(response.unwrap())
        } else {
            response.toLeft()
        }
    }

    suspend fun get(url: String): Either<ApiException, String> {
        // Get token if necessary
        var token = if (settings.authToken == null) {
            val getTokenResult = login()
            if (getTokenResult.isLeft()) {
                return getTokenResult.toLeft()
            }

            getTokenResult.unwrap().token
        } else {
            settings.authToken.orEmpty()
        }

        settings.authToken = token

        // Do request
        val response = httpClient.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return Either.Right(response.bodyAsText())
        }

        // Authenticate was unsuccessful, retry request
        return if (response.status == HttpStatusCode.Unauthorized) {
            val getTokenResult = login()
            if (getTokenResult.isLeft()) {
                return getTokenResult.toLeft()
            }

            token = getTokenResult.unwrap().token
            settings.authToken = token

            // Do request
            val retryResponse = httpClient.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
            }

            if (retryResponse.status == HttpStatusCode.OK) {
                Either.Right(retryResponse.bodyAsText())
            } else {
                Either.Left(InvalidHttpStatusCodeException(response.status))
            }
        } else {
            Either.Left(InvalidHttpStatusCodeException(response.status))
        }
    }

    suspend fun login(): Either<ApiException, LoginResponse> {
        val body = Json.encodeToString(
            LoginRequest(
                username = "admin",
                password = "abc123"
            )
        )

        val response = httpClient.post(buildLoginUrl()) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        return if (response.status == HttpStatusCode.OK) {
            parseJson(response.bodyAsText())
        } else {
            Either.Left(InvalidHttpStatusCodeException(response.status))
        }
    }

    private inline fun <reified T> parseJson(body: String): Either<ApiException, T> {
        return try {
            Either.Right(Json.decodeFromString<T>(body))
        } catch (exception: SerializationException) {
            Timber.d(exception)
            Either.Left(ApiException(cause = exception))
        }
    }
}