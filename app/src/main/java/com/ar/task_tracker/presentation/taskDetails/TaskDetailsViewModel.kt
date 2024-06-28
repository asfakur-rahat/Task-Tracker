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
    var done = MutableLiveData<Boolean>(false)
        private set

    fun saveImage(task: Task) = viewModelScope.launch {
        println("here")
        val response = repository.saveTaskDetailsInCloud(task)

        if(response == true){
            done.value = response
        }else{
            done.value = response
        }
    }
}