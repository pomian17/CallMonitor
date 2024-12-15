package com.mpomian.callmonitor.data.provider.real

import android.content.ContentResolver
import android.provider.CallLog
import com.mpomian.callmonitor.data.model.LoggedCall
import com.mpomian.callmonitor.data.provider.base.CallLogProvider
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Real implementation of [CallLogProvider] that fetches call logs from the device's call log.
 * It retrieves the call logs once when created and then listens for changes in the call log.
 */
class RealCallLogProvider(
    private val contentResolver: ContentResolver,
    callLogObserver: CallLogObserver,
    coroutineScope: CoroutineScope
) : CallLogProvider {

    private val _callLogsFlow = MutableStateFlow<List<LoggedCall>>(emptyList())

    init {
        val newLogs = fetchCallLogs()
        _callLogsFlow.value = newLogs

        callLogObserver.callLogsFlow.onEach {
            val newLogs: List<LoggedCall> = fetchCallLogs()
            _callLogsFlow.value = newLogs
        }.launchIn(coroutineScope)
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
