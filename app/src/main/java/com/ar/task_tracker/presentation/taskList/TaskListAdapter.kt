package com.ar.task_tracker.presentation.taskList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ar.task_tracker.databinding.TaskItemBinding
import com.ar.task_tracker.domain.model.Task

class TaskListAdapter(
    private val onTaskClicked: (Task) -> Unit
): ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffUtil) {
    class TaskViewHolder(
        private val binding: TaskItemBinding,
        private val onTaskClicked: (Task) -> Unit

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            if(task.image == null) {
                binding.taskImage.visibility = View.GONE
            }else{
                binding.taskImage.visibility = View.VISIBLE
                binding.taskImage.load(task.image)
            }
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description
            binding.taskStatus.text = if (task.status) "Completed" else "Pending"
            binding.taskStartTime.text = task.startTime
            binding.taskDeadline.text = task.deadline
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding, onTaskClicked)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val TaskDiffUtil = object : DiffUtil.ItemCallback<Task>(){
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }

        }
    }

}