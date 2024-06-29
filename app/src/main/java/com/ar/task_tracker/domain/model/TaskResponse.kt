package com.ar.task_tracker.domain.model


// Task Response data class from JSONPlaceHolder API
data class TaskResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
