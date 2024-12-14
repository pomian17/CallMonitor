package com.mpomian.callmonitor.repository.mock

import com.mpomian.callmonitor.model.OngoingCall
import com.mpomian.callmonitor.repository.base.CallStatusProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockCallStatusProvider : CallStatusProvider {
    override val ongoingCall: StateFlow<OngoingCall> = MutableStateFlow(
        OngoingCall(
            ongoing = false,
            number = null,
            name = null
        )
    )
}