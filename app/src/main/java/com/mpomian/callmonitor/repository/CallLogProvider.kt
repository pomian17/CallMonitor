package com.mpomian.callmonitor.repository

import android.content.ContentResolver
import android.provider.CallLog
import com.mpomian.callmonitor.model.LoggedCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CallLogProvider(private val contentResolver: ContentResolver) : CallLogRepository {

    override fun getCallLogs(): Flow<List<LoggedCall>> = flow {
        val callLogs = mutableListOf<LoggedCall>()

        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION,
                CallLog.Calls.DATE,
                CallLog.Calls.CACHED_NAME
            ),
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )


        val numberIndex = cursor?.getColumnIndex(CallLog.Calls.NUMBER)?.takeIf { it >= 0 }
        val durationIndex = cursor?.getColumnIndex(CallLog.Calls.DURATION)?.takeIf { it >= 0 }
        val dateIndex = cursor?.getColumnIndex(CallLog.Calls.DATE)?.takeIf { it >= 0 }
        val nameIndex = cursor?.getColumnIndex(CallLog.Calls.CACHED_NAME)?.takeIf { it >= 0 }

        cursor?.use {
            while (it.moveToNext()) {

                val number = numberIndex?.let { index -> it.getString(index) }
                val duration = durationIndex?.let { index -> it.getLong(index) }
                val date = dateIndex?.let { index -> it.getLong(index) }
                val name = nameIndex?.let { index -> it.getString(index) }

                if (number == null || duration == null || date == null) {
                    continue
                }
                callLogs.add(
                    LoggedCall(
                        number = number,
                        duration = duration,
                        beginning = date.toString(), //TODO add mapper
                        name = name
                    )
                )
            }
        }

        emit(callLogs)
    }
}
