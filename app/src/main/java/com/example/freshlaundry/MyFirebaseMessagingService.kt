package com.example.freshlaundry

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    class MyFirebaseMessagingService : FirebaseMessagingService() {

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)

            Log.d("FCM", "Pesan diterima dari: ${remoteMessage.from}")

            val title = remoteMessage.notification?.title ?: "Pesan Baru"
            val body = remoteMessage.notification?.body ?: "Ada pembaruan pesanan."
            val orderId = remoteMessage.data["orderId"] ?: ""

            Log.d("FCM", "Judul: $title")
            Log.d("FCM", "Isi: $body")
            Log.d("FCM", "Order ID: $orderId")
            Log.d("FCM", "Data: ${remoteMessage.data}")

            showNotification(title, body, orderId)
        }

        private fun showNotification(title: String, body: String, orderId: String) {
            Log.d("FCM", "Menampilkan notifikasi: $title - $body")

            val channelId = "fresh_laundry_channel"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("orderId", orderId)
            }

            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Fresh Laundry Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notificationId = System.currentTimeMillis().toInt()
            Log.d("FCM", "Mengirim notifikasi dengan ID: $notificationId")
            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        override fun onNewToken(token: String) {
            super.onNewToken(token)
            Log.d("FCM", "Token baru: $token")
            // Kirim token ke server jika diperlukan
        }
    }
    }