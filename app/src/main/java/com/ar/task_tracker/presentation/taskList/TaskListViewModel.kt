package com.ar.task_tracker.presentation.taskList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import com.ar.task_tracker.utils.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
        private val repository: ListRepository
) : ViewModel() {
        var taskList = MutableLiveData<List<Task>>()
            private set
        var isLoading = MutableLiveData<Boolean>()
            private set

        init {
            viewModelScope.launch {
                isLoading.value = true
                val response = repository.getTasks()
                if (response.isSuccessful) {
                    val taskResponseList = response.body()?.toList()
                    val taskList = mutableListOf<Task>()
                    taskResponseList?.forEach { taskResponse ->
                        var image: String? = null
                        if (taskResponse.id % 2 == 1) {
                            image = AppConstant.Default_Task_Image
                        }
                        taskList.add(
                            Task(
                                id = taskResponse.id,
                                title = taskResponse.title,
                                description = AppConstant.Default_Task_Description,
                                image = image,
                                status = taskResponse.completed,
                                startTime = "Now",
                                deadline = "1 Hour",
                            ),
                        )
                    }
                    isLoading.value = false
                    this@TaskListViewModel.taskList.value = taskList
                }
            }
        }
}
