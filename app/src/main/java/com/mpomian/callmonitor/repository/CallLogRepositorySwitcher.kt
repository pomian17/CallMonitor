package com.mpomian.callmonitor.repository

import android.content.ContentResolver
import com.mpomian.callmonitor.model.CallLog
import kotlinx.coroutines.flow.Flow

class CallLogRepositorySwitcher(
    contentResolver: ContentResolver,
    private val hasPermission: Boolean
) : CallLogRepository {

    private val mockCallRepository = MockCallRepository()
    private val callLogProvider = CallLogProvider(contentResolver)

    override fun getCallLogs(): Flow<List<CallLog>> {
        return if (hasPermission) {
            callLogProvider.getCallLogs()
        } else {
            mockCallRepository.getCallLogs()
        }
    }
}