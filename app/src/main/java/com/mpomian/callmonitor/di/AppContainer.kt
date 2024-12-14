package com.mpomian.callmonitor.di

import android.Manifest
import android.content.Context
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.base.CallLogRepository
import com.mpomian.callmonitor.repository.mock.MockCallRepository
import com.mpomian.callmonitor.repository.mock.MockCallStatusProvider
import com.mpomian.callmonitor.repository.mock.MockContactResolver
import com.mpomian.callmonitor.repository.real.CallLogProvider
import com.mpomian.callmonitor.repository.real.RealCallStatusProvider
import com.mpomian.callmonitor.repository.real.RealContactResolver

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

    val callRepository: CallLogRepository by lazy {
        if (hasCallLogPermission) {
            CallLogProvider(context.contentResolver)
        } else {
            MockCallRepository()
        }
    }

    private val telephonyManager: TelephonyManager by lazy {
        context.getSystemService(TelephonyManager::class.java)
    }

    private val contactResolver by lazy {
        if (hasContactPermission) {
            RealContactResolver(context.contentResolver)
        } else {
            MockContactResolver()
        }
    }

    val callStatusProvider by lazy {
        if (hasCallStatusPermission) {
            RealCallStatusProvider(telephonyManager, contactResolver)
        } else {
            MockCallStatusProvider()
        }
    }

    val httpServer: HttpServer by lazy { HttpServer(callRepository, callStatusProvider) }
}