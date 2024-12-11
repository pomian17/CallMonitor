package com.mpomian.callmonitor.di

import android.content.Context
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.CallLogRepository
import com.mpomian.callmonitor.repository.CallLogRepositorySwitcher

class AppContainer(context: Context) {
    val callRepository: CallLogRepository by lazy {
        CallLogRepositorySwitcher(
            contentResolver = context.contentResolver,
            hasPermission = false //TODO unmock
        )
    }
    val httpServer: HttpServer by lazy { HttpServer(callRepository) }
}