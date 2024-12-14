package com.mpomian.callmonitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.mpomian.callmonitor.data.model.LoggedCall
import com.mpomian.callmonitor.data.model.OngoingCall
import com.mpomian.callmonitor.data.model.ServerState
import com.mpomian.callmonitor.data.network.HttpServer
import com.mpomian.callmonitor.data.repository.base.CallLogRepository
import com.mpomian.callmonitor.data.repository.base.CallStatusProvider
import com.mpomian.callmonitor.utils.Utils.getDeviceIpAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallLogListViewModel(
    repository: CallLogRepository,
    callStatusProvider: CallStatusProvider,
    httpServer: HttpServer
) : ViewModel() {

    val callLogs: StateFlow<List<LoggedCall>> = repository.getCallLogs()

    private val _deviceIp = MutableStateFlow("fetching...")
    val deviceIp: StateFlow<String> = _deviceIp

    private val _serverStatus = httpServer.serverState
    val serverStatus: StateFlow<ServerState> = _serverStatus

    private val _callStatus = callStatusProvider.ongoingCall
    val callStatus: StateFlow<OngoingCall> = _callStatus

    init {
        fetchDeviceIp()
    }

    private fun fetchDeviceIp() {
        _deviceIp.value = getDeviceIpAddress()
    }
}