package com.mpomian.callmonitor.di

import android.Manifest
import android.content.Context
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.mpomian.callmonitor.data.network.HttpServer
import com.mpomian.callmonitor.data.provider.base.CallLogProvider
import com.mpomian.callmonitor.data.provider.mock.MockCallProvider
import com.mpomian.callmonitor.data.provider.mock.MockCallStatusProvider
import com.mpomian.callmonitor.data.provider.mock.MockContactNameProvider
import com.mpomian.callmonitor.data.provider.real.RealCallLogProvider
import com.mpomian.callmonitor.data.provider.real.RealCallStatusProvider
import com.mpomian.callmonitor.data.provider.real.RealContactNameProvider

class AppContainer(context: Context) {

    val hasCallLogPermission: Boolean by lazy {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PermissionChecker.PERMISSION_GRANTED
    }
    val hasCallStatusPermission by lazy {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PermissionChecker.PERMISSION_GRANTED
    }
    val hasContactPermission by lazy {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    val callLogProvider: CallLogProvider by lazy {
        if (hasCallLogPermission) {
            RealCallLogProvider(context.contentResolver)
        } else {
            MockCallProvider()
        }
    }

    private val telephonyManager: TelephonyManager by lazy {
        context.getSystemService(TelephonyManager::class.java)
    }

    private val contactResolver by lazy {
        if (hasContactPermission) {
            RealContactNameProvider(context.contentResolver)
        } else {
            MockContactNameProvider()
        }
    }

    val callStatusProvider by lazy {
        if (hasCallStatusPermission) {
            RealCallStatusProvider(telephonyManager, contactResolver)
        } else {
            MockCallStatusProvider()
        }
    }

    val httpServer: HttpServer by lazy { HttpServer(callLogProvider, callStatusProvider) }
}