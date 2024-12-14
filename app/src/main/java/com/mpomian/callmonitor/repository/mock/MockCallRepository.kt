package com.mpomian.callmonitor.repository.mock

import com.mpomian.callmonitor.model.LoggedCall
import com.mpomian.callmonitor.repository.base.CallLogRepository
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MockCallRepository : CallLogRepository {

    private val _listFlow = MutableStateFlow<List<LoggedCall>>(emptyList())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            var initialCallLogs = listOf(
                LoggedCall(
                    beginning = (System.currentTimeMillis() - 60 * 60 * 1000).toFormattedDate(),
                    duration = 112,
                    number = "123456789",
                    name = "John Doe"
                ),
                LoggedCall(
                    beginning = (System.currentTimeMillis() - 90 * 60 * 1000).toFormattedDate(),
                    duration = 30,
                    number = "987654321",
                    name = "Jane Doe"
                ),
                LoggedCall(
                    beginning = (System.currentTimeMillis() - 120 * 60 * 1000).toFormattedDate(),
                    duration = 75,
                    number = "1122334455",
                    name = null
                )
            )
            _listFlow.value = initialCallLogs
            while (true) {
                delay(5000)
                val randomCallLog = LoggedCall(
                    beginning = (System.currentTimeMillis()).toFormattedDate(),
                    duration = (10..120).random().toLong(),
                    number = "${(100000000..999999999).random()}",
                    name = null
                )
                _listFlow.value = listOf(randomCallLog) + _listFlow.value
            }
        }
    }

    override fun getCallLogs(): StateFlow<List<LoggedCall>> = _listFlow
}

