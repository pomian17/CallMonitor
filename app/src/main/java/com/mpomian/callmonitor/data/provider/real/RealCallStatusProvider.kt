@file:Suppress("DEPRECATION")

package com.mpomian.callmonitor.data.provider.real

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.mpomian.callmonitor.data.model.OngoingCall
import com.mpomian.callmonitor.data.provider.base.CallStatusProvider
import com.mpomian.callmonitor.data.provider.base.ContactNameProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Real implementation of [CallStatusProvider] that uses [TelephonyManager] to get the call status.
 * This class uses deprecated API due to lack of direct replacement in newer Android versions.
 */
class RealCallStatusProvider(telephonyManager: TelephonyManager, contactNameProvider: ContactNameProvider) :
    CallStatusProvider {

    private val _ongoingCall = MutableStateFlow(
        OngoingCall(
            ongoing = false,
            number = null,
            name = null
        )
    )
    override val ongoingCall: StateFlow<OngoingCall> = _ongoingCall

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE, TelephonyManager.CALL_STATE_RINGING -> {
                    _ongoingCall.value = OngoingCall(
                        ongoing = false,
                        number = null,
                        name = null
                    )
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    val callerName = phoneNumber?.let { contactNameProvider.getContactName(it) }
                    _ongoingCall.value = OngoingCall(
                        ongoing = true,
                        number = phoneNumber,
                        name = callerName
                    )
                }
            }
        }
    }

    init {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
}
