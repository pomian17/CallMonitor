package com.mpomian.callmonitor.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel
import java.text.SimpleDateFormat

@Composable
fun CallLogListScreen(viewModel: CallLogListViewModel, modifier: Modifier = Modifier) {
    val callLogs by viewModel.callLogs.collectAsState()
    val deviceIp by viewModel.deviceIp.collectAsState()
    var serverStatus by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Device IP: $deviceIp")
        Text("Server Status: ${if (serverStatus) "Running" else "Stopped"}")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { serverStatus = viewModel.startServer() }
            ) {
                Text("Start Server")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.stopServer()
                    serverStatus = false
                }
            ) { Text("Stop Server") }
        }

        LazyColumn {
            items(callLogs.size) { index ->
                val call = callLogs[index]
                CallLogItem(call)
            }
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
