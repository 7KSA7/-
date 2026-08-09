package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    const val CHANNEL_SECURITY_ALERTS = "channel_vip_security_alerts"
    const val CHANNEL_REALTIME_STATUS = "channel_vip_realtime_status"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alertChannel = NotificationChannel(
                CHANNEL_SECURITY_ALERTS,
                "VIP Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical security alerts, malware detections and network blocks"
            }

            val statusChannel = NotificationChannel(
                CHANNEL_REALTIME_STATUS,
                "VIP Protection Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Real-time background security monitoring status"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(statusChannel)
        }
    }

    fun showThreatAlert(context: Context, title: String, message: String, isCritical: Boolean = false) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_SECURITY_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🛡️ VIP Protection: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }
}
