package com.mpomian.callmonitor.repository

import com.mpomian.callmonitor.model.CallLog
import kotlinx.coroutines.flow.Flow

interface CallLogRepository {
    fun getCallLogs(): Flow<List<CallLog>>
}