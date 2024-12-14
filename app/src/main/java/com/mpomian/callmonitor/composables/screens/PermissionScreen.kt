package com.mpomian.callmonitor.composables.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.mpomian.callmonitor.R


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    modifier: Modifier,
    navigateToCallLogList: () -> Unit,
) {
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        listOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
        )
    }
    val (recomposeTrigger, setRecomposeTrigger) = remember { mutableIntStateOf(0) }
    val permissionsState = rememberMultiplePermissionsState(requiredPermissions) {
        setRecomposeTrigger(recomposeTrigger + 1)
    }

    val context = LocalContext.current

    when {
        permissionsState.allPermissionsGranted -> {
            LaunchedEffect(Unit) {
                navigateToCallLogList()
            }
        }

        isFirstLaunch(context) -> {
            FirstLaunchLayout(
                modifier = modifier,
                requiredPermissions = requiredPermissions,
            ) {
                permissionsState.launchMultiplePermissionRequest()
                setFirstLaunchFlag(context, false)
            }
        }

        else -> {
            EnablePermissionsRationaleLayout(
                modifier = modifier,
                canAskForPermission = permissionsState.permissions.any { it.status.shouldShowRationale },
                onProceed = { navigateToCallLogList() },
                permissionState = permissionsState
            )
        }
    }
}

@Composable
fun FirstLaunchLayout(
    modifier: Modifier,
    requiredPermissions: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.first_launch_permission_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        requiredPermissions.mapNotNull { getPermissionDescriptionResId(it) }.forEach { resId ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(resId),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClick) {
            Text(text = stringResource(R.string.grant_permissions))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnablePermissionsRationaleLayout(
    modifier: Modifier,
    onProceed: () -> Unit,
    canAskForPermission: Boolean,
    permissionState: MultiplePermissionsState
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.permissions_rationale_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        permissionState.revokedPermissions.mapNotNull {
            getPermissionNotGrantedDescriptionResId(it.permission)
        }.forEach { resId ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(resId),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (canAskForPermission) {
            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                Text(text = stringResource(R.string.grant_permissions))
            }
        } else {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            ) {
                Text(text = stringResource(R.string.open_settings))
            }
        }
        Button(onClick = { onProceed() }) {
            Text(text = stringResource(R.string.proceed))
        }
    }
}

fun getPermissionDescriptionResId(permission: String): Int? {
    return when (permission) {
        Manifest.permission.READ_CALL_LOG -> R.string.read_call_log_permission_title
        Manifest.permission.READ_CONTACTS -> R.string.read_contacts_permission_title
        Manifest.permission.READ_PHONE_STATE -> R.string.read_phone_state_permission_title
        Manifest.permission.POST_NOTIFICATIONS -> R.string.post_notifications_permission_title
        else -> null
    }
}

fun getPermissionNotGrantedDescriptionResId(permission: String): Int? {
    return when (permission) {
        Manifest.permission.READ_CALL_LOG -> R.string.read_call_log_permission_not_granted
        Manifest.permission.READ_CONTACTS -> R.string.read_contacts_permission_not_granted
        Manifest.permission.READ_PHONE_STATE -> R.string.read_phone_state_permission_not_granted
        Manifest.permission.POST_NOTIFICATIONS -> R.string.post_notifications_permission_not_granted
        else -> null
    }
}

fun isFirstLaunch(context: Context): Boolean {
    val sharedPreferences =
        context.getSharedPreferences("CallLogAppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isFirstLaunch", true)
}

fun setFirstLaunchFlag(context: Context, isFirstLaunch: Boolean) {
    val sharedPreferences =
        context.getSharedPreferences("CallLogAppPreferences", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("isFirstLaunch", isFirstLaunch).apply()
}
