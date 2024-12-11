package com.mpomian.callmonitor.repository

import com.mpomian.callmonitor.model.CallLog

class MockCallRepository {
    fun getCallLogs(): List<CallLog> {
        return listOf(
            CallLog("2018-05-02T23:00:00+00:00", 112, "+123456789", "John Doe"),
            CallLog("2018-05-02T23:00:00+00:00", 30, "+987654321", "Jane Doe"),
            CallLog("2018-05-02T23:00:00+00:00", 75, "+1122334455", null)
        )
    }
}
