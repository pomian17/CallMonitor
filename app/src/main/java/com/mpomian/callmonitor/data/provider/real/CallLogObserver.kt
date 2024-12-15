package com.mpomian.callmonitor.data.provider.real

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * CallLogObserver is responsible for observing changes in the call log
 * and emitting a signal as callLogsFlow when a change occurs.
 */
class CallLogObserver(contentResolver: ContentResolver) {

    private val _callLogsFlow = MutableSharedFlow<Unit>()
    val callLogsFlow: SharedFlow<Unit> = _callLogsFlow

    private val callLogObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            _callLogsFlow.tryEmit(Unit)
        }
    }

    init {
        contentResolver.registerContentObserver(
            CallLog.Calls.CONTENT_URI,
            true,
            callLogObserver
        )
    }

}