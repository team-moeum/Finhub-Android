package com.fotcamp.finhub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FinhubMessagingService: FirebaseMessagingService() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "finhubNotificationChannelId"
        const val NOTIFICATION_CHANNEL_NAME = "핀허브 알림"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"] ?: ""
        val body = message.data["body"] ?: ""

        createNotification(title, body, message.data)
    }

    private fun createNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("push", true)
        intent.putExtra("view", data["view"] ?: "")

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        builder.setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        manager.notify(0, builder.build())
    }
}