package com.mpomian.callmonitor.utils

import io.ktor.utils.io.printStack
import java.net.InetAddress
import java.net.NetworkInterface

object Utils {
    fun getDeviceIpAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces().toList()
            interfaces.asSequence()
                .flatMap { it.inetAddresses.asSequence() }
                .filter {
                    !it.isLoopbackAddress
                            && it is InetAddress
                            && it.hostAddress?.contains('.') == true
                }
                .map { it.hostAddress }
                .firstOrNull() ?: "Unavailable"
        } catch (e: Exception) {
            e.printStack()
            "Unavailable"
        }
    }
}