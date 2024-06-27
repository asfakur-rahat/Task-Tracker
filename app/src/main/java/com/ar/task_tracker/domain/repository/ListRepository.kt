package com.ar.task_tracker.domain.repository


import com.ar.task_tracker.domain.model.TaskResponse
import retrofit2.Response

interface ListRepository {
    suspend fun getTasks() : Response<List<TaskResponse>>
}