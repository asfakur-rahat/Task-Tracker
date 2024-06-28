package com.ar.task_tracker.presentation.taskDetails

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskDetailsBinding
import com.ar.task_tracker.domain.model.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private lateinit var binding: FragmentTaskDetailsBinding
    private val args: TaskDetailsFragmentArgs by navArgs()
    private lateinit var task: Task
    private val viewModel: TaskDetailsViewModel by viewModels()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
            if (uri!=null){
                handleImage(uri)
            }
        }
    }

    private fun handleImage(uri: Uri) {
        task = task.copy(
            image = uri.toString()
        )
        binding.ivTaskImage.setImageURI(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTaskDetailsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        task = args.taskItem
        initView(task)
        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.topAppBar.setOnMenuItemClickListener {  item ->
            when(item.itemId){
                R.id.delete -> saveTask(
                    task
                )
            }
            true
        }
    }

    private fun saveTask(taskItem: Task) {
        viewModel.saveImage(taskItem)
    }

    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun initView(task: Task) {
        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        if(task.image == null){
            binding.ivTaskImage.visibility = View.GONE
        }else{
            binding.ivTaskImage.load(task.image)
        }
        binding.tvStartTime.text = task.startTime
        binding.tvDeadline.text = task.deadline
        if(task.status){
            binding.tvTaskStatus.text = "Completed"
            binding.tvTaskStatus.setTextColor(Color.GREEN)
        }
        else{
            binding.tvTaskStatus.text = "Pending"
            binding.tvTaskStatus.setTextColor(Color.RED)
        }
    }


}