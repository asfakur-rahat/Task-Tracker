package com.ar.task_tracker.data.firebase.firebase_service

import com.ar.task_tracker.domain.model.FireBaseResponse
import com.ar.task_tracker.domain.model.Task

interface FirebaseService {
    suspend fun getTaskDetailsFromFireBase(): List<FireBaseResponse>
    suspend fun saveTaskDetailsInFireBase(task: Task): Boolean
    suspend fun deleteTask(taskId: Int, task: Task): Boolean
}