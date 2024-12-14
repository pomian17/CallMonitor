package com.mpomian.callmonitor.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.mpomian.callmonitor.model.LoggedCall
import com.mpomian.callmonitor.service.ServerForegroundService
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallLogListScreen(viewModel: CallLogListViewModel, modifier: Modifier = Modifier) {
    val callLogPermissionState = rememberPermissionState(Manifest.permission.READ_CALL_LOG)
    val callStatePermissionState = rememberPermissionState(Manifest.permission.READ_PHONE_STATE)
    val readContactsPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    val context = LocalContext.current
    val callLogs by viewModel.callLogs.collectAsState()
    val deviceIp by viewModel.deviceIp.collectAsState()
    val serverStatus by viewModel.serverStatus.collectAsState()
    val ongoingCall by viewModel.callStatus.collectAsState()
    val listState = rememberLazyListState()

    val requestCallLogPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //TODO
    }
    val requestNotificationsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //TODO
    }
    val requestCallStatePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //TODO
    }

    val requestReadContactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //TODO
    }

    LaunchedEffect(callLogPermissionState, notificationsPermissionState) {
        if (!callLogPermissionState.status.isGranted && callLogPermissionState.status.shouldShowRationale) {
            // TODO Show rationale
        } else {
            requestCallLogPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
        }

        if (!callStatePermissionState.status.isGranted && callStatePermissionState.status.shouldShowRationale) {
            // TODO Show rationale
        } else {
            requestCallStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }

        if (!readContactsPermissionState.status.isGranted && readContactsPermissionState.status.shouldShowRationale) {
            // TODO Show rationale
        } else {
            requestReadContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && notificationsPermissionState != null) {
            if (!notificationsPermissionState.status.isGranted && notificationsPermissionState.status.shouldShowRationale) {
                // TODO Show rationale
            } else {
                requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(callLogs) {
        if (listState.firstVisibleItemIndex in 0..1) {
            listState.animateScrollToItem(0)
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

        Text(
            modifier = Modifier.padding(16.dp),
            text = ongoingCall.toString()
        )

        LazyColumn(state = listState) {
            itemsIndexed(callLogs, key = { _, item -> item.beginning }) { _, call ->
                CallLogItem(call)
            }
        }
    }
}

@Composable
fun CallLogItem(call: LoggedCall) {
    Column(modifier = Modifier.padding(8.dp)) {
        BasicText(text = "Name: ${call.name ?: "Unknown"}")
        BasicText(text = "Number: ${call.number}")
        BasicText(text = "Duration: ${call.duration} seconds")
        BasicText(text = "Time: ${call.beginning}")
    }
}
