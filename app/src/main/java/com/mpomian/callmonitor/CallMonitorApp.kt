package com.mpomian.callmonitor

import android.app.Application
import com.mpomian.callmonitor.di.AppContainer


class CallMonitorApp : Application() {
    val appContainer = AppContainer(this)
}