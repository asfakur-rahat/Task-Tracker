package com.ar.task_tracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ar.task_tracker.data.local.dao.TaskDao
import com.ar.task_tracker.domain.model.Task


@Database(
    entities = [Task::class],
    version = 1, exportSchema = false
)
abstract  class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        operator fun invoke(context: Context) = buildDatabase(context)
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                name = "Task.db"
            ).build()
        }
    }
}