package com.ar.task_tracker.presentation.taskDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {
    var deleted = MutableStateFlow(false)
        private set

    var loader = MutableStateFlow(false)
        private set

    fun deleteTask(taskID: Int, task: Task) = viewModelScope.launch {
        loader.value = true
        deleted.value = false
        repository.deleteTaskFromDB(taskID)
        repository.deleteTaskFromCloud(taskID,task)
        loader.value = false
        deleted.value = true
    }


}