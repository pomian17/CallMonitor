package com.mpomian.callmonitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mpomian.callmonitor.data.network.HttpServer
import com.mpomian.callmonitor.data.provider.base.CallLogProvider
import com.mpomian.callmonitor.data.provider.base.CallStatusProvider

class ViewModelFactory(
    private val callLogProvider: CallLogProvider,
    private val callStatusProvider: CallStatusProvider,
    private val httpServer: HttpServer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallLogListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallLogListViewModel(
                callLogProvider = callLogProvider,
                callStatusProvider = callStatusProvider,
                httpServer = httpServer
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}