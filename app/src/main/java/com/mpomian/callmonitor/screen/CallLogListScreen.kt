package com.mpomian.callmonitor.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mpomian.callmonitor.model.CallLog
import com.mpomian.callmonitor.service.ServerForegroundService
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallLogListScreen(viewModel: CallLogListViewModel, modifier: Modifier = Modifier) {

    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    val context = LocalContext.current
    val callLogs by viewModel.callLogs.collectAsState()
    val deviceIp by viewModel.deviceIp.collectAsState()
    val serverStatus by viewModel.serverStatus.collectAsState()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //TODO
    }

    LaunchedEffect(notificationsPermissionState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && notificationsPermissionState != null) {
            if (!notificationsPermissionState.status.isGranted && notificationsPermissionState.status.shouldShowRationale) {
                // TODO Show rationale
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


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
                onClick = {
                    val intent = Intent(context, ServerForegroundService::class.java)
                    context.startService(intent)
                }
            ) {
                Text("Start Server")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val intent = Intent(context, ServerForegroundService::class.java)
                    context.stopService(intent)
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
        BasicText(            text = "Time: ${call.beginning}"        )
    }
}
