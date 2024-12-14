package com.mpomian.callmonitor.data.repository.base

import com.mpomian.callmonitor.data.model.LoggedCall
import kotlinx.coroutines.flow.StateFlow

interface CallLogRepository {
    fun getCallLogs(): StateFlow<List<LoggedCall>>
}