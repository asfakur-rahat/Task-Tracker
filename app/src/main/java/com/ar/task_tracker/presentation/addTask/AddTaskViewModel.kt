package com.ar.task_tracker.presentation.addTask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {
    var cloudDone = MutableLiveData<Boolean>(false)
        private set
    var availableID = MutableLiveData<Int>(0)
        private set
    var allDone = MutableLiveData<Boolean>(false)
        private set
    var loader = MutableLiveData<Boolean>(false)
        private set

    fun currentTaskCount() = viewModelScope.launch {
        val response = repository.getTasksFromDb()
        val ids = response.map {
            it.id
        }
        var id = 1
        while(id in ids){
            id++
            println(id)
        }
        availableID.value = id
    }

    private fun saveTaskToRoom(taskList: List<Task>) = viewModelScope.launch {
        repository.insertTasks(taskList)
        loader.value = false
        allDone.value = true
    }
    fun fetchTaskFromCloud(taskID: Int, task: Task) = viewModelScope.launch{
        val response = repository.getTaskFromCloud()
        val taskList = mutableListOf<Task>()
        for(item in response){
            if(item.id == taskID){
                taskList.add(
                    task.copy(
                        image = item.image
                    )
                )
            }
        }
        saveTaskToRoom(taskList)
    }

    fun saveTaskToCloud(task: Task) = viewModelScope.launch {
        loader.value = true
        val response = repository.saveTaskDetailsInCloud(task)
        cloudDone.value = response
    }
}