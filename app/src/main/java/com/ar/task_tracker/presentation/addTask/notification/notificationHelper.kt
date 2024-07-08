package com.ar.task_tracker.presentation.addTask.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ar.task_tracker.R

fun showNotification(context: Context, title: String, message: String) {
    val builder = NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, builder.build())
}
