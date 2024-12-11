package com.mpomian.callmonitor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mpomian.callmonitor.CallMonitorApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServerForegroundService : Service() {

    private val server by lazy { (application as CallMonitorApp).appContainer.httpServer }
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createNotificationChannel()
            startForeground(
                NOTIFICATION_ID,
                createNotification("Server ready to start"),
                FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification("Server ready to start"))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!server.isRunning.value) {
            serviceScope.launch { startServer() }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.launch { stopServer() }
        super.onDestroy()
    }

    private fun startServer() {
        val startResult = try {
            server.start()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        updateNotification("Server status: ${if (startResult) "Running" else "Failure"}")
    }

    private fun stopServer() {
        server.stop()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createNotificationChannel() {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Server Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HTTP Server")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "ServerChannel"
        private const val NOTIFICATION_ID = 1
    }
}
