package com.ar.task_tracker.data.network.api

import com.ar.task_tracker.domain.model.TaskResponse
import retrofit2.Response
import retrofit2.http.GET

interface TaskApi {

    @GET("todos")
    suspend fun getTasks(): Response<List<TaskResponse>>
}