package com.mpomian.callmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mpomian.callmonitor.screen.CallLogListScreen
import com.mpomian.callmonitor.ui.theme.CallMonitorTheme
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as CallMonitorApp).appContainer

        enableEdgeToEdge()
        setContent {
            CallMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = CallLogListViewModel(
                        appContainer.mockCallRepository,
                        appContainer.httpServer
                    )

                    setContent {
                        CallLogListScreen(
                            viewModel = viewModel, modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}