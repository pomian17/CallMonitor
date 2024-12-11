package com.mpomian.callmonitor.di

import com.mpomian.callmonitor.network.HttpServer

class AppContainer {
    val httpServer: HttpServer by lazy { HttpServer() }
}