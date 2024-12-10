package com.mpomian.callmonitor.network

import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class HttpServer {
    private var server: NettyApplicationEngine? = null

    fun start() {
        val hostAddress = getDeviceIpAddress()
        val port = 8080
        server = embeddedServer(Netty, host = hostAddress, port = port) {
            routing {
                get("/") {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val formattedDate = dateFormat.format(Date())

                    call.respond(
                        HttpStatusCode.OK,
                        Json.encodeToString(
                            RootResponse(
                                start = formattedDate,
                                services = listOf(
                                    Service("status", "$hostAddress:$port/status"),
                                    Service("log", "$hostAddress:$port/log")
                                )
                            )
                        )
                    )
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 1000)
    }
}

@Serializable
data class RootResponse(
    val start: String,
    val services: List<Service>
)

@Serializable
data class Service(
    val name: String,
    val uri: String
)