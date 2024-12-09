package com.mpomian.callmonitor.repository

import com.mpomian.callmonitor.model.CallLog

class MockCallRepository {
    fun getCallLogs(): List<CallLog> {
        return listOf(
            CallLog("John Doe", "+123456789", 112, System.currentTimeMillis() - 3003000),
            CallLog("Jane Doe", "+987654321", 30, System.currentTimeMillis() - 7007000),
            CallLog(null, "+1122334455", 75, System.currentTimeMillis() - 10001000)
        )
    }
}
