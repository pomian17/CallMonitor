package com.mpomian.callmonitor.presentation.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mpomian.callmonitor.R
import com.mpomian.callmonitor.data.model.LoggedCall
import com.mpomian.callmonitor.data.model.ServerState
import com.mpomian.callmonitor.presentation.viewmodel.CallLogListViewModel
import com.mpomian.callmonitor.service.ServerForegroundService

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallLogListScreen(viewModel: CallLogListViewModel, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val callLogs by viewModel.callLogs.collectAsState()
    val deviceIp by viewModel.deviceIp.collectAsState()
    val serverStatus by viewModel.serverStatus.collectAsState()
    val ongoingCall by viewModel.callStatus.collectAsState()
    val listState = rememberLazyListState()

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
        Text(stringResource(R.string.device_ip, deviceIp))
        Text(
            stringResource(
                R.string.server_status,
                when (serverStatus) {
                    ServerState.STARTING -> stringResource(R.string.starting)
                    ServerState.RUNNING -> stringResource(R.string.running)
                    ServerState.STOPPING -> stringResource(R.string.stopping)
                    ServerState.STOPPED -> stringResource(R.string.stopped)
                }
            )
        )
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
                Text(stringResource(R.string.start_server))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val intent = Intent(context, ServerForegroundService::class.java)
                    context.stopService(intent)
                }
            ) { Text(stringResource(R.string.stop_server)) }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(
                R.string.name,
                call.name ?: stringResource(R.string.unknown)
            )
        )
        Text(text = stringResource(R.string.number, call.number))
        Text(text = stringResource(R.string.duration_seconds, call.duration))
        Text(text = stringResource(R.string.time, call.beginning))
    }
}
