package com.mpomian.callmonitor.data.provider.base

import com.mpomian.callmonitor.data.model.OngoingCall
import kotlinx.coroutines.flow.StateFlow

interface CallStatusProvider {
    val ongoingCall: StateFlow<OngoingCall>
}