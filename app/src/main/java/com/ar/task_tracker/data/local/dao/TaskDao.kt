package com.ar.task_tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ar.task_tracker.domain.model.Task

// Task Data Access Object
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Query("SELECT * FROM tasks")
    suspend fun getTasks(): List<Task>

    @Query("DELETE FROM tasks WHERE id = :taskID")
    suspend fun deleteTask(taskID: Int)
}