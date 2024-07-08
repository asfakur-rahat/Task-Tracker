package com.ar.task_tracker.presentation.addTask.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent){
        val taskTitle = intent.getStringExtra("task_title") ?: "Task"
        showNotification(context, "Pending Task Reminder", "Your task titled as [$taskTitle] is about to expire in less than 15 minutes.")
    }
}
