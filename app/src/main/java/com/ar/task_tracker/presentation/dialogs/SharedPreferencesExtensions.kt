// SharedPreferencesExtensions.kt

package com.ar.task_tracker.presentation.dialogs

import android.content.SharedPreferences
import com.google.gson.Gson

fun <T> SharedPreferences.putData(key: String, data: T) {
    val json = Gson().toJson(data)
    edit().putString(key, json).apply()
}

inline fun <reified T> SharedPreferences.getData(key: String): T? {
    val json = getString(key, null) ?: return null
    return Gson().fromJson(json, T::class.java)
}
