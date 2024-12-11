package com.mpomian.callmonitor.viewmodel

import androidx.lifecycle.ViewModel
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.MockCallRepository
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallLogListViewModel(httpServer: HttpServer) : ViewModel() {
    private val repository = MockCallRepository()

    private val _callLogs = MutableStateFlow<List<CallLog>>(emptyList())
    val callLogs: StateFlow<List<CallLog>> = _callLogs

    private val _deviceIp = MutableStateFlow("fetching...")
    val deviceIp: StateFlow<String> = _deviceIp

    private val _serverStatus = httpServer.isRunning
    val serverStatus: StateFlow<Boolean> = _serverStatus

    init {
        loadCallLogs()
        fetchDeviceIp()
    }

    private fun loadCallLogs() {
        _callLogs.value = repository.getCallLogs()
    }

    private fun fetchDeviceIp() {
        _deviceIp.value = getDeviceIpAddress()
    }
}