package com.ar.task_tracker.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize



// Task Response From Room Database
@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val image: String? = null,
    val status: Boolean = false,
    val startTime: String,
    val deadline: String
): Parcelable
