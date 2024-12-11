package com.mpomian.callmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.CallLogRepository
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CallLogListViewModel(
    private val repository: CallLogRepository,
    httpServer: HttpServer
) : ViewModel() {

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
        viewModelScope.launch {
            repository.getCallLogs().collect {
                _callLogs.value = it
            }
        }
    }

    private fun fetchDeviceIp() {
        _deviceIp.value = getDeviceIpAddress()
    }
}