package com.mpomian.callmonitor.network

import com.mpomian.callmonitor.model.CallLogWithQueryCount
import com.mpomian.callmonitor.repository.base.CallLogRepository
import com.mpomian.callmonitor.repository.base.CallStatusProvider
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

class HttpServer(
    private val callLogRepository: CallLogRepository,
    private val callStatusProvider: CallStatusProvider
) {

    private var server: NettyApplicationEngine? = null
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    private lateinit var formattedStartDate: String
    private val queryCountMap = ConcurrentHashMap<String, Int>()

    //TODO: Add full error handling and constants for port
    fun start() {
        val hostAddress = getDeviceIpAddress()
        val port = 8080

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        formattedStartDate = dateFormat.format(Date())

        server = embeddedServer(Netty, host = hostAddress, port = port) {
            routing {
                get("/") {
                    call.respondJson(
                        Json.encodeToString(
                            RootResponse(
                                start = formattedStartDate,
                                services = listOf(
                                    Service("status", "$hostAddress:$port/status"),
                                    Service("log", "$hostAddress:$port/log")
                                )
                            )
                        )
                    )
                }

                get("/status") {
                    try {
                        val ongoingCall = callStatusProvider.ongoingCall.value
                        call.respondJson(Json.encodeToString(ongoingCall))
                    } catch (e: Exception) {
                        println("Error fetching call status: ${e.message}")
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            "Unable to fetch call status"
                        )
                    }
                }

                get("/log") {
                    try {
                        val callLogsWithQueryCount =
                            callLogRepository.getCallLogs().value.map { log ->
                                val timesQueried = queryCountMap[log.beginning] ?: 0
                                queryCountMap.put(log.beginning, timesQueried + 1)
                                CallLogWithQueryCount(
                                    beginning = log.beginning,
                                    duration = log.duration,
                                    number = log.number,
                                    name = log.name,
                                    timesQueried = timesQueried
                                )
                            }
                        call.respondJson(Json.encodeToString(callLogsWithQueryCount))
                    } catch (e: Exception) {
                        println("Error fetching call logs: ${e.message}")
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            "Unable to fetch call logs"
                        )
                    }
                }
            }
        }.start(wait = false)
        _isRunning.value = true
    }

    fun stop() {
        server?.stop(1000, 1000)
        _isRunning.value = false
    }

    private suspend fun ApplicationCall.respondJson(response: String) {
        try {
            respondText(
                response,
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        } catch (e: Exception) {
            println("Error generating response: ${e.message}")
            respond(HttpStatusCode.InternalServerError, "Error processing request")
        }
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