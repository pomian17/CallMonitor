package com.mpomian.callmonitor.data.network

import com.mpomian.callmonitor.data.model.CallLogWithQueryCount
import com.mpomian.callmonitor.data.model.ServerState
import com.mpomian.callmonitor.data.repository.base.CallLogRepository
import com.mpomian.callmonitor.data.repository.base.CallStatusProvider
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.KtorDsl
import io.ktor.util.pipeline.PipelineInterceptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class HttpServer(
    private val callLogRepository: CallLogRepository,
    private val callStatusProvider: CallStatusProvider
) {

    private var server: NettyApplicationEngine? = null
    private val _serverState = MutableStateFlow(ServerState.STOPPED)
    val serverState: StateFlow<ServerState> = _serverState
    private lateinit var formattedStartDate: String
    private val queryCountMap = ConcurrentHashMap<String, Int>()

    fun start() {
        _serverState.value = ServerState.STARTING
        val hostAddress = getDeviceIpAddress()

        formattedStartDate = System.currentTimeMillis().toFormattedDate()

        server = embeddedServer(Netty, host = hostAddress, port = PORT) {
            routing {
                safeGet(ROOT) {
                    call.respondJson(
                        Json.encodeToString(
                            RootResponse(
                                start = formattedStartDate,
                                services = listOf(
                                    Service("status", "$hostAddress:$PORT$STATUS"),
                                    Service("log", "$hostAddress:$PORT$LOG")
                                )
                            )
                        )
                    )
                }

                safeGet(STATUS) {
                    val ongoingCall = callStatusProvider.ongoingCall.value
                    call.respondJson(Json.encodeToString(ongoingCall))
                }

                safeGet(LOG) {
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

                }
            }
        }.start(wait = false).also {
            it.environment.monitor.subscribe(ApplicationStopped) {
                _serverState.value = ServerState.STOPPED
            }
        }
        _serverState.value = ServerState.RUNNING
    }

    fun stop() {
        _serverState.value = ServerState.STOPPING
        server?.stop(GRACE_PERIOD, SHUTDOWN_TIMEOUT)
    }

    private suspend fun ApplicationCall.respondJson(response: String) =
        respondText(
            response,
            ContentType.Application.Json,
            HttpStatusCode.OK
        )

    @KtorDsl
    private fun Route.safeGet(
        path: String,
        operation: PipelineInterceptor<Unit, ApplicationCall>
    ): Route {
        return get(path) {
            try {
                operation(Unit)
            } catch (e: Exception) {
                println("Error generating response for $path: $e")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error processing request")
            }
        }
    }

    companion object {
        const val ROOT = "/"
        const val LOG = "/log"
        const val STATUS = "/status"
        const val PORT = 8080
        const val GRACE_PERIOD = 1000L
        const val SHUTDOWN_TIMEOUT = 1000L
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