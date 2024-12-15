package com.mpomian.callmonitor

import android.app.Application
import com.mpomian.callmonitor.di.DependencyProvider


class CallMonitorApp : Application() {
    val dependencyProvider = DependencyProvider(this)
}