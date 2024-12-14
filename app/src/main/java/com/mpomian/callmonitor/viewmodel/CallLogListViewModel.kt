package com.mpomian.callmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpomian.callmonitor.model.LoggedCall
import com.mpomian.callmonitor.model.OngoingCall
import com.mpomian.callmonitor.network.HttpServer
import com.mpomian.callmonitor.repository.CallLogRepository
import com.mpomian.callmonitor.repository.CallStatusProvider
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CallLogListViewModel(
    private val repository: CallLogRepository,
    callStatusProvider: CallStatusProvider,
    httpServer: HttpServer
) : ViewModel() {

    private val _callLogs = MutableStateFlow<List<LoggedCall>>(emptyList())
    val callLogs: StateFlow<List<LoggedCall>> = _callLogs

    private val _deviceIp = MutableStateFlow("fetching...")
    val deviceIp: StateFlow<String> = _deviceIp

    private val _serverStatus = httpServer.isRunning
    val serverStatus: StateFlow<Boolean> = _serverStatus

    private val _callStatus = callStatusProvider.ongoingCall
    val callStatus: StateFlow<OngoingCall> = _callStatus

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