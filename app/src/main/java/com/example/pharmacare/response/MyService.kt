package com.example.pharmacare.response

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pharmacare.R
import com.example.pharmacare.model.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {

    private val CHANNEL_ID = "NOTIFY_CHANNEL"
    private var notificationId = 1

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val loadList: ArrayList<Order> = intent.getParcelableArrayListExtra("myList") ?: return START_NOT_STICKY

        createNotificationChannel()
        showNotifications(loadList)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notify Products",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for product availability notifications"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotifications(list: ArrayList<Order>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (subscription in list) {

                val title = "🚨 Product Expiry Alert 🚨"
                val line1 = "Hey, User! 😔"
                val line2 = "Your product ${subscription.itemname} has expired or will expire soon."
                val line3 = "It was supposed to expire on: ${subscription.dateOfExpiry}."
                val actionText = "Don't worry! Visit us for fresh products 🍃"

                val notification = NotificationCompat.Builder(this@MyService, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(line1)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .bigText("$line1\n$line2\n$line3\n$actionText")
                    )
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                NotificationManagerCompat.from(this@MyService).notify(notificationId++, notification)

                delay(180_000L)
            }

            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}