package com.mpomian.callmonitor.viewmodel
import androidx.lifecycle.ViewModel
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.repository.MockCallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallLogListViewModel : ViewModel() {
    private val repository = MockCallRepository()

    private val _callLogs = MutableStateFlow<List<CallLog>>(emptyList())
    val callLogs: StateFlow<List<CallLog>> = _callLogs

    init {
        loadCallLogs()
    }

    private fun loadCallLogs() {
        _callLogs.value = repository.getCallLogs()
    }
}