package com.github.aivanovski.picoautomator.web

import com.github.aivanovski.picoautomator.web.di.WebAppModule
import com.github.aivanovski.picoautomator.web.presentation.configureAuthentication
import com.github.aivanovski.picoautomator.web.presentation.routes.configureRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(WebAppModule.module)
    }

    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        configureAuthentication()
        configureRouting()
    }.start(wait = true)
}


