package com.mpomian.callmonitor.data.provider.base

import com.mpomian.callmonitor.data.model.LoggedCall
import kotlinx.coroutines.flow.StateFlow

interface CallLogProvider {
    fun getCallLogs(): StateFlow<List<LoggedCall>>
}