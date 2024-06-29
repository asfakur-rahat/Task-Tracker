package com.ar.task_tracker.domain.model


//Task response from Firebase firestore
data class FireBaseResponse(
    val id: Int,
    val description: String,
    val image: String? = null
)
