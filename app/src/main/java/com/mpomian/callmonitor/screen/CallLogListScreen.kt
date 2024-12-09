package com.mpomian.callmonitor.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel
import java.text.SimpleDateFormat

@Composable
fun CallLogListScreen(viewModel: CallLogListViewModel, modifier: Modifier = Modifier) {
    val callLogs by viewModel.callLogs.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(callLogs.size) { index ->
            val call = callLogs[index]
            CallLogItem(call)
        }
    }
}

@Composable
fun CallLogItem(call: CallLog) {
    Column(modifier = Modifier.padding(8.dp)) {
        BasicText(text = "Name: ${call.name ?: "Unknown"}")
        BasicText(text = "Number: ${call.number}")
        BasicText(text = "Duration: ${call.duration} seconds")
        BasicText(
            text = "Time: ${
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(call.timestamp)
            }"
        )
    }
}
