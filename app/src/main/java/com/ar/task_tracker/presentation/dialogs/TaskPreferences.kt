package com.ar.task_tracker.presentation.dialogs

// TaskPreferences.kt

import android.content.Context
import com.ar.task_tracker.domain.model.Task

fun saveTask(context: Context, task: Task) {
    val sharedPreferences = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
    sharedPreferences.putData("task_key", task)
}

fun getTask(context: Context): Task? {
    val sharedPreferences = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getData("task_key")
}

fun saveStartDate(context: Context, date: String) {
    val sharedPreferences = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("start_date", date).apply()
}

fun getStartDate(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("start_date", null)
}


