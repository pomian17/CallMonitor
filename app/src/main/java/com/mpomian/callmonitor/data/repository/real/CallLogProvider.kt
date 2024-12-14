package com.mpomian.callmonitor.data.repository.real

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import com.mpomian.callmonitor.data.model.LoggedCall
import com.mpomian.callmonitor.data.repository.base.CallLogRepository
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallLogProvider(private val contentResolver: ContentResolver) : CallLogRepository {

    private val _callLogsFlow = MutableStateFlow<List<LoggedCall>>(emptyList())

    private val callLogObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            val newLogs = fetchCallLogs()
            _callLogsFlow.value = newLogs
        }
    }

    init {
        contentResolver.registerContentObserver(
            CallLog.Calls.CONTENT_URI,
            true,
            callLogObserver
        )

        val newLogs = fetchCallLogs()
        _callLogsFlow.value = newLogs
    }

    override fun getCallLogs(): StateFlow<List<LoggedCall>> = _callLogsFlow

    private fun fetchCallLogs(): List<LoggedCall> {
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
                        beginning = date.toFormattedDate(),
                        name = name
                    )
                )
            }
        }

        return callLogs
    }
}
