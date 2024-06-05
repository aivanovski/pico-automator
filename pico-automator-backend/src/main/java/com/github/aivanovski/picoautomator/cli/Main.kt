package com.github.aivanovski.picoautomator.cli

import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText

fun main() {
    embeddedServer(Netty, 8080) {
        install(WebSockets) {
            pingPeriodMillis = 15_000
            timeoutMillis = 15_000
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }

            webSocket("/echo") {
                send(Frame.Text("CONNECTED"))

                for (frame in incoming) {
                    val textFrame = frame as? Frame.Text ?: continue

                    val receivedText = textFrame.readText()
                    when {
                        receivedText == "START-FLOW" -> {
                            send(Frame.Text("STEP 1: launch 'org.wikipedia'"))
                        }

                        receivedText.startsWith("STEP-1") -> {
                            send(Frame.Text("STEP 2: tap-on 'Search Wikipedia'"))
                        }

                        receivedText == "CLOSE" -> {
                            close(
                                CloseReason(
                                    CloseReason.Codes.NORMAL,
                                    "Session is closed by client request"
                                )
                            )
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
