package com.ar.task_tracker.domain.repository


import com.ar.task_tracker.domain.model.FireBaseResponse
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.model.TaskResponse
import retrofit2.Response

interface ListRepository {
    suspend fun getTasks() : Response<List<TaskResponse>>

    suspend fun insertTasks(tasks: List<Task>)

    suspend fun getTasksFromDb() : List<Task>

    suspend fun searchTasks(query: String) : List<Task>

    suspend fun deleteTaskFromDB(taskID: Int)

    suspend fun saveTaskDetailsInCloud(task: Task): Boolean

    suspend fun getTaskFromCloud(): List<FireBaseResponse>

    suspend fun deleteTaskFromCloud(taskID: Int, task: Task): Boolean

    //suspend fun updateTaskInCloud(task: Task): Boolean
}