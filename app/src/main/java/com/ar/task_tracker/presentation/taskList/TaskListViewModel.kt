package com.ar.task_tracker.presentation.taskList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import com.ar.task_tracker.utils.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel
@Inject
constructor(
    private val repository: ListRepository,
) : ViewModel() {
    var taskList = MutableStateFlow<List<Task>>(listOf())
        private set
    var isLoading = MutableStateFlow(false)
        private set

    fun initList() =
        viewModelScope.launch {
            isLoading.value = true
            val responseFromDB = repository.getTasksFromDb()
            delay(500)
            if (responseFromDB.isNotEmpty()) {
                isLoading.value = false
                this@TaskListViewModel.taskList.value = responseFromDB
            } else {
                val response = repository.getTasks()
                if (response.isSuccessful) {
                    val taskResponseList = response.body()?.toList()
                    val taskList = mutableListOf<Task>()
                    var count = 0
                    taskResponseList?.forEach { taskResponse ->
                        val task = Task(
                            id = taskResponse.id,
                            title = taskResponse.title,
                            description = AppConstant.Default_Task_Description,
                            image = null,
                            status = taskResponse.completed,
                            startTime = "Now",
                            deadline = "1 Hour",
                        )
                        taskList.add(
                            task
                        )
                        val result = repository.saveTaskDetailsInCloud(task)
                        if (result) {
                            count++
                        }
                    }
                    if (count == taskResponseList?.size) {
                        isLoading.value = false
                    }
                    repository.insertTasks(taskList)
                    this@TaskListViewModel.taskList.value = taskList
                }
            }
        }
    fun searchTask(query: String) = viewModelScope.launch {
        isLoading.value = true
        val response = repository.searchTasks(query)
        delay(500)
        taskList.value = response
        isLoading.value = false
    }

    fun markTaskAsDone(task: Task) = viewModelScope.launch {
        val taskList = mutableListOf(task)
        repository.insertTasks(taskList)
        val updatedList = repository.getTasksFromDb()
        this@TaskListViewModel.taskList.value = updatedList
    }

}

