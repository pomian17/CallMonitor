package com.mpomian.callmonitor.data.provider.mock

import com.mpomian.callmonitor.data.model.OngoingCall
import com.mpomian.callmonitor.data.provider.base.CallStatusProvider
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