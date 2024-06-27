package com.ar.task_tracker.domain.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val image: String? = null,
    val status: Boolean = false,
    val startTime: String,
    val deadline: String
)
