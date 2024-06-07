package com.github.aivanovski.picoautomator.web.entity

data class JwtData(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
) {

    companion object {
        // TODO: data should be in resources
        val DEFAULT = JwtData(
            secret = "secret",
            issuer = "http://0.0.0.0:8080/",
            audience = "http://0.0.0.0:8080/hello",
            realm = "Access to Pico Automator API",
        )
    }
}