package com.ar.task_tracker.data

import com.ar.task_tracker.data.firebase.Firebase
import com.ar.task_tracker.data.local.AppDatabase
import com.ar.task_tracker.data.network.api.TaskApi
import com.ar.task_tracker.domain.model.FireBaseResponse
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.model.TaskResponse
import com.ar.task_tracker.domain.repository.ListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


// Implementation of ListRepository
class ListRepositoryImpl @Inject constructor(
    private val api: TaskApi,
    private val db: AppDatabase
): ListRepository {

    override suspend fun getTasks(): Response<List<TaskResponse>> = withContext(Dispatchers.IO) {
        return@withContext api.getTasks()
    }

    override suspend fun insertTasks(tasks: List<Task>) = withContext(Dispatchers.IO) {
        return@withContext db.taskDao().insertTasks(tasks)
    }

    override suspend fun getTasksFromDb(): List<Task> = withContext(Dispatchers.IO) {
        return@withContext db.taskDao().getTasks()
    }

    override suspend fun saveTaskDetailsInCloud(task: Task): Boolean {
        return Firebase.saveTaskDetailsInFireBase(task)
    }

    override suspend fun getTaskFromCloud(): List<FireBaseResponse> {
        return Firebase.getTaskDetailsFromFireBase()
    }

    override suspend fun deleteTaskFromCloud(taskID: Int, task: Task): Boolean {
        return Firebase.deleteTask(taskID, task)
    }

    override suspend fun deleteTaskFromDB(taskID: Int) {
        return db.taskDao().deleteTask(taskID)
    }
}