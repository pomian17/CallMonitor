package com.mpomian.callmonitor.composables

import android.content.Intent
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mpomian.callmonitor.R
import com.mpomian.callmonitor.model.LoggedCall
import com.mpomian.callmonitor.service.ServerForegroundService
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel

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
                if (serverStatus) {
                    stringResource(R.string.running)
                } else {
                    stringResource(R.string.stopped)
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
    Column(modifier = Modifier.padding(8.dp)) {
        BasicText(
            text = stringResource(
                R.string.name,
                call.name ?: stringResource(R.string.unknown)
            )
        )
        BasicText(text = stringResource(R.string.number, call.number))
        BasicText(text = stringResource(R.string.duration_seconds, call.duration))
        BasicText(text = stringResource(R.string.time, call.beginning))
    }
}
