package com.mpomian.callmonitor.repository

import com.mpomian.callmonitor.model.LoggedCall
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockCallRepository : CallLogRepository {
    override fun getCallLogs(): Flow<List<LoggedCall>> = flow {
        var callLogs = listOf(
            LoggedCall(
                beginning = (System.currentTimeMillis() - 60 * 60 * 1000).toFormattedDate(),
                duration = 112,
                number = "+123456789",
                name = "John Doe"
            ),
            LoggedCall(
                beginning = (System.currentTimeMillis() - 90 * 60 * 1000).toFormattedDate(),
                duration = 30,
                number = "+987654321",
                name = "Jane Doe"
            ),
            LoggedCall(
                beginning = (System.currentTimeMillis() - 120 * 60 * 1000).toFormattedDate(),
                duration = 75,
                number = "+1122334455",
                name = null
            )
        )
        emit(callLogs)
        while (true) {
            delay(5000)
            val randomCallLog = LoggedCall(
                beginning = (System.currentTimeMillis()).toFormattedDate(),
                duration = (10..120).random().toLong(),
                number = "+${(100000000..999999999).random()}",
                name = null
            )
            callLogs = listOf(randomCallLog) + callLogs
            emit(callLogs)
        }

    }
}
