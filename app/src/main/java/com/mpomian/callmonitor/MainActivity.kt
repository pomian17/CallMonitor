package com.mpomian.callmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mpomian.callmonitor.presentation.navigation.MainNavHost
import com.mpomian.callmonitor.presentation.viewmodel.CallLogListViewModel
import com.mpomian.callmonitor.presentation.viewmodel.ViewModelFactory
import com.mpomian.callmonitor.ui.theme.CallMonitorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as CallMonitorApp).appContainer

        val viewModel: CallLogListViewModel by viewModels {
            ViewModelFactory(
                appContainer.callLogProvider,
                appContainer.callStatusProvider,
                appContainer.httpServer
            )
        }
        enableEdgeToEdge()
        setContent {
            CallMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}