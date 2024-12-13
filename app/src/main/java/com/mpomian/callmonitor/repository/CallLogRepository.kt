package com.mpomian.callmonitor.repository

import com.mpomian.callmonitor.model.LoggedCall
import kotlinx.coroutines.flow.Flow

interface CallLogRepository {
    fun getCallLogs(): Flow<List<LoggedCall>>
}