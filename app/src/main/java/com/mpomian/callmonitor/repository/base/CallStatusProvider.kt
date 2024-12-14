package com.mpomian.callmonitor.repository.base

import com.mpomian.callmonitor.model.OngoingCall
import kotlinx.coroutines.flow.StateFlow

interface CallStatusProvider {
    val ongoingCall: StateFlow<OngoingCall>
}