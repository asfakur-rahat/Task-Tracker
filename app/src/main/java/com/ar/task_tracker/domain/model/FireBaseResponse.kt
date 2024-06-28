package com.ar.task_tracker.domain.model

data class FireBaseResponse(
    val id: Int,
    val description: String,
    val image: String? = null
)
