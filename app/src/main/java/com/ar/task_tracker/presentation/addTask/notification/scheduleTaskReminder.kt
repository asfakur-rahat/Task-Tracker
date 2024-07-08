package com.ar.task_tracker.presentation.addTask.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.concurrent.TimeUnit

fun scheduleTaskReminder(context: Context,taskID: Int, taskTitle: String, deadline: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, TaskReminderReceiver::class.java).apply {
        putExtra("task_title", taskTitle)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, taskID, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val currentTime = System.currentTimeMillis()
    val triggerAtMillis = deadline - TimeUnit.MINUTES.toMillis(15)

    try {
        Log.d("TaskReminder", "Scheduling task reminder for $taskTitle at $triggerAtMillis")
        if (triggerAtMillis > currentTime) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            // Task deadline is less than 15 minutes from now, set the alarm immediately (or after 30 seconds)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15), pendingIntent)
        }
    } catch (e: SecurityException) {
        Log.d("TaskReminder", "Security exception occurred: ${e.message}")
        if (triggerAtMillis > currentTime) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15), pendingIntent)
        }
    }
}
