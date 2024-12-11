package com.mpomian.callmonitor.di

import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.MockCallRepository

class AppContainer {
    val mockCallRepository: MockCallRepository by lazy { MockCallRepository() }
    val httpServer: HttpServer by lazy { HttpServer(mockCallRepository) }
}