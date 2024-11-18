package com.example.barcode.Receivers

import NetworkMonitor
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NetworkMonitoringService : Service() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())

        networkMonitor = NetworkMonitor(this) {


        }
        networkMonitor.startMonitoring()

    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.stopMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "NetworkMonitorService",
                "Network Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "NetworkMonitorService")
            .setContentTitle("Monitoring Network")
            .setContentText("The app is monitoring network connectivity.")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .build()
    }


}
