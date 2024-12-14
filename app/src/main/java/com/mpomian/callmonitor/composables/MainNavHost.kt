package com.mpomian.callmonitor.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mpomian.callmonitor.di.AppContainer
import com.mpomian.callmonitor.viewmodel.CallLogListViewModel

@Composable
fun MainNavHost(
    appContainer: AppContainer,
    modifier: Modifier
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "permissions") {
        composable("permissions") {
            PermissionsScreen(
                modifier = modifier,
                navigateToCallLogList = { navController.navigate("call_log_list") },
            )
        }
        composable("call_log_list") {
            val viewModel = CallLogListViewModel(
                appContainer.callRepository,
                appContainer.callStatusProvider,
                appContainer.httpServer
            )
            CallLogListScreen(viewModel = viewModel, modifier = modifier)
        }
    }
}