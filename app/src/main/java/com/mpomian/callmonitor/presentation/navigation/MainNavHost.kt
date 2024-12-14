package com.mpomian.callmonitor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mpomian.callmonitor.di.AppContainer
import com.mpomian.callmonitor.presentation.screen.CallLogListScreen
import com.mpomian.callmonitor.presentation.screen.PermissionsScreen
import com.mpomian.callmonitor.presentation.viewmodel.CallLogListViewModel

const val PERMISSIONS_ROUTE = "permissions"
const val CALL_LOG_LIST_ROUTE = "call_log_list"

@Composable
fun MainNavHost(
    appContainer: AppContainer,
    modifier: Modifier
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = PERMISSIONS_ROUTE) {
        composable(PERMISSIONS_ROUTE) {
            PermissionsScreen(
                modifier = modifier,
                navigateToCallLogList = {
                    navController.navigate(CALL_LOG_LIST_ROUTE) {
                        popUpTo(PERMISSIONS_ROUTE) { inclusive = true }
                    }
                },
            )
        }
        composable(CALL_LOG_LIST_ROUTE) {
            val viewModel = CallLogListViewModel(
                appContainer.callLogProvider,
                appContainer.callStatusProvider,
                appContainer.httpServer
            )
            CallLogListScreen(viewModel = viewModel, modifier = modifier)
        }
    }
}