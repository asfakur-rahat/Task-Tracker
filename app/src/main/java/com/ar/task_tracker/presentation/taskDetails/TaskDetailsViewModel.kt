package com.ar.task_tracker.presentation.taskDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {
    var updated = MutableLiveData<Boolean>(false)
        private set

    fun deleteTask(taskID: Int) = viewModelScope.launch {
        updated.value = false
        repository.deleteTaskFromDB(taskID)
        repository.deleteTaskFromCloud(taskID)
        updated.value = true
    }


}