package com.mpomian.callmonitor.viewmodel

import androidx.lifecycle.ViewModel
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.MockCallRepository
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallLogListViewModel : ViewModel() {
    private val repository = MockCallRepository()

    private val _callLogs = MutableStateFlow<List<CallLog>>(emptyList())
    val callLogs: StateFlow<List<CallLog>> = _callLogs

    private val _deviceIp = MutableStateFlow("fetching...")
    val deviceIp: StateFlow<String> = _deviceIp

    private val server = HttpServer()

    init {
        loadCallLogs()
        fetchDeviceIp()
    }

    fun startServer(): Boolean {
        return try {
            server.start()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stopServer() {
        server.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopServer()
    }

    private fun loadCallLogs() {
        _callLogs.value = repository.getCallLogs()
    }

    private fun fetchDeviceIp() {
        _deviceIp.value = getDeviceIpAddress()
    }
}