package com.mpomian.callmonitor.utils

import io.ktor.utils.io.printStack
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    fun Long.toFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT)
        dateFormat.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return dateFormat.format(Date(this))
    }

}