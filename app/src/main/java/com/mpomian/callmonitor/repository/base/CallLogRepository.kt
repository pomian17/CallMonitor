package com.mpomian.callmonitor.repository.base

import com.mpomian.callmonitor.model.LoggedCall
import kotlinx.coroutines.flow.StateFlow

interface CallLogRepository {
    fun getCallLogs(): StateFlow<List<LoggedCall>>
}