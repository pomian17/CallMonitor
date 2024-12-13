package com.mpomian.callmonitor.di

import android.content.Context
import android.telephony.TelephonyManager
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.CallLogProvider
import com.mpomian.callmonitor.repository.CallLogRepository
import com.mpomian.callmonitor.repository.MockCallRepository
import com.mpomian.callmonitor.repository.CallStatusProvider
import com.mpomian.callmonitor.repository.ContactResolver

class AppContainer(context: Context) {
    val hasPermission = true //TODO unmock
    val callRepository: CallLogRepository by lazy {
        if (hasPermission) {
            CallLogProvider(context.contentResolver)
        } else {
            MockCallRepository()
        }
    }
    private val telephonyManager: TelephonyManager by lazy {
        context.getSystemService(TelephonyManager::class.java)
    }
    private val contactResolver by lazy {
        //TODO null if no permission
        ContactResolver(context.contentResolver)
    }
    val callStatusProvider by lazy {
        //TODO mock if no permission
        CallStatusProvider(telephonyManager, contactResolver)
    }
    val httpServer: HttpServer by lazy { HttpServer(callRepository, callStatusProvider) }
}