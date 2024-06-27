package com.ar.task_tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val image: String? = null,
    val status: Boolean = false,
    val startTime: String,
    val deadline: String
)
