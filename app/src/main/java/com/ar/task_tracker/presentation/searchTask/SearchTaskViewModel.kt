package com.ar.task_tracker.presentation.searchTask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchTaskViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {

    var loader = MutableLiveData<Boolean>(false)
        private set
    var taskList = MutableLiveData<List<Task>>()
        private set

    fun searchTask(query: String) = viewModelScope.launch {
        loader.value = true
        val response = repository.searchTasks(query)
        taskList.value = response
        loader.value = false
    }

    fun markTaskAsDone(task: Task, query: String?) = viewModelScope.launch {
        val taskList = mutableListOf(task)
        repository.insertTasks(taskList)
        searchTask(query!!)
    }

}