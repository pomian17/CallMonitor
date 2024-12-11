package com.mpomian.callmonitor.repository

import android.content.ContentResolver
import android.provider.CallLog as AndroidCallLog
import com.mpomian.callmonitor.model.CallLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CallLogProvider(private val contentResolver: ContentResolver) : CallLogRepository {

    override fun getCallLogs(): Flow<List<CallLog>> = flow {
        val callLogs = mutableListOf<CallLog>()

        val cursor = contentResolver.query(
            AndroidCallLog.Calls.CONTENT_URI,
            arrayOf(
                AndroidCallLog.Calls.NUMBER,
                AndroidCallLog.Calls.DURATION,
                AndroidCallLog.Calls.DATE,
                AndroidCallLog.Calls.CACHED_NAME
            ),
            null,
            null,
            AndroidCallLog.Calls.DATE + " DESC"
        )



        val numberIndex = cursor?.getColumnIndex(AndroidCallLog.Calls.NUMBER)?.takeIf { it >= 0 }
        val durationIndex = cursor?.getColumnIndex(AndroidCallLog.Calls.DURATION)?.takeIf { it >= 0 }
        val dateIndex = cursor?.getColumnIndex(AndroidCallLog.Calls.DATE)?.takeIf { it >= 0 }
        val nameIndex = cursor?.getColumnIndex(AndroidCallLog.Calls.CACHED_NAME)?.takeIf { it >= 0 }

        cursor?.use {
            while (it.moveToNext()) {

                val number = numberIndex?.let { index -> it.getString(index) }
                val duration = durationIndex?.let { index -> it.getLong(index) }
                val date = dateIndex?.let { index -> it.getLong(index) }
                val name = nameIndex?.let { index -> it.getString(index) }

                if(number == null || duration == null || date == null) {
                    continue
                }
                callLogs.add(
                    CallLog(
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
