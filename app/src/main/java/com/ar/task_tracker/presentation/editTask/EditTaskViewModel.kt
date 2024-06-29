package com.ar.task_tracker.presentation.editTask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val repository: ListRepository
): ViewModel(){
    var cloudDone = MutableLiveData<Boolean>(false)
        private set
    var allDone = MutableLiveData<Boolean>(false)
        private set

    var loader = MutableLiveData<Boolean>(false)
        private set


    private fun saveTaskToRoom(taskList: List<Task>) = viewModelScope.launch {
        repository.insertTasks(taskList)
        loader.value = false
        allDone.value = true
    }
    fun fetchTaskFromCloud(taskID: Int,imageUri: String?, task: Task) = viewModelScope.launch{
        val taskList = mutableListOf<Task>()
        if (imageUri == null){
            taskList.add(
                task
            )
            saveTaskToRoom(taskList)
        }else{
            val response = repository.getTaskFromCloud()
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

    }

    fun saveTaskToCloud(task: Task, imageUri: String?) = viewModelScope.launch {
        loader.value = true
        val newTask = if (imageUri == null){
            task.copy(image = null)
        }else{
            task.copy(image = imageUri)
        }
        val response = repository.saveTaskDetailsInCloud(newTask)
        cloudDone.value = response
    }
}