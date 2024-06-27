package com.ar.task_tracker.domain.model

data class TaskResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
