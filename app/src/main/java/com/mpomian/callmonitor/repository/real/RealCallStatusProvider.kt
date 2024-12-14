@file:Suppress("DEPRECATION")

package com.mpomian.callmonitor.repository.real

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.mpomian.callmonitor.model.OngoingCall
import com.mpomian.callmonitor.repository.base.CallStatusProvider
import com.mpomian.callmonitor.repository.base.ContactResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This class uses deprecated API due to lack of replacement in newer versions.
 */
class RealCallStatusProvider(telephonyManager: TelephonyManager, contactResolver: ContactResolver) :
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
                    val callerName = phoneNumber?.let { contactResolver.getContactName(it) }
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
