package com.ar.task_tracker.presentation.addTask

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentAddTaskBinding
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.presentation.dialogs.DatePickerFragment
import com.ar.task_tracker.presentation.dialogs.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : Fragment(R.layout.fragment_add_task) {

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: AddTaskViewModel by viewModels()
    private var imageuri: String? = null
    private var taskId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
                handleImage(uri)
            }
        }
    }

    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleImage(uri: Uri) {
        imageuri = uri.toString()
        binding.ivTaskImage.setImageURI(uri)
    }

    private fun saveTask(taskItem: Task) {
        viewModel.saveTaskToCloud(taskItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddTaskBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()

    }

    private fun initObserver() {
        viewModel.taskCount.observe(viewLifecycleOwner) {
            taskId = it + 1
        }
        viewModel.cloudDone.observe(viewLifecycleOwner) {
            println(it)
            if(it == true){
                viewModel.fetchTaskFromCloud(
                    taskId,
                    Task(
                        id = taskId,
                        title = binding.tvTaskTitle.text.toString().trimMargin(),
                        description = binding.tvTaskDescription.text.toString().trimMargin(),
                        image = imageuri,
                        startTime = binding.tvStartTime.text.toString().trimMargin(),
                        deadline = binding.tvDeadline.text.toString().trimMargin(),
                        status = false
                    )
                )
            }
        }
        viewModel.allDone.observe(viewLifecycleOwner) {
            //println(it)
            if (it == true) {
                //println("eihane")
                findNavController().popBackStack()
            }
        }
    }

    private fun initView() {
        viewModel.currentTaskCount()
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save -> {
                    //println("Menu Item Clicked")
                    saveTask(
                        Task(
                            id = taskId,
                            title = binding.tvTaskTitle.text.toString().trimMargin(),
                            description = binding.tvTaskDescription.text.toString().trimMargin(),
                            image = imageuri,
                            startTime = binding.tvStartTime.text.toString().trimMargin(),
                            deadline = binding.tvDeadline.text.toString().trimMargin(),
                            status = false
                        )
                    )
                }
            }
            true
        }
        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.tvStartTime.setOnClickListener {
            val newFragment = DatePickerFragment { yy, mm, dd ->
                binding.tvStartTime.text = "$yy/$mm/$dd"
                val timeFragment = TimePickerFragment { hh, mm ->
                    binding.tvStartTime.setText(binding.tvStartTime.text.toString() + " - $hh:$mm")
                }
                timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
            }
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }
        binding.tvDeadline.setOnClickListener {
            val newFragment = DatePickerFragment { yy, mm, dd ->
                binding.tvDeadline.text = "$yy/$mm/$dd"
                val timeFragment = TimePickerFragment { hh, min ->
                    binding.tvDeadline.setText(binding.tvDeadline.text.toString() + " - $hh:$min")
                }
                timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
            }
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }
    }
}