package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class ReceiverDismiss: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        p0?.let{
            val notificationManagerCompat = NotificationManagerCompat.from(it)
            notificationManagerCompat.cancel(1)
        }

    }
}