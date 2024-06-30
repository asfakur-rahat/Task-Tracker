package com.ar.task_tracker.presentation.addTask

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Calendar

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

    // Picked image handler
    private fun handleImage(uri: Uri) {
        imageuri = uri.toString()
        binding.ivTaskImage.setImageURI(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddTaskBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    // LiveData observer
    private fun initObserver() {
        viewModel.availableID.observe(viewLifecycleOwner) {
            taskId = it
        }
        viewModel.cloudDone.observe(viewLifecycleOwner) {
            //println(it)
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
            if (it == true) {
                goBack()
            }
        }

        viewModel.loader.observe(viewLifecycleOwner){
            if(it ==true){
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {
        viewModel.currentTaskCount()
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save -> {
                    saveTaskClicked()
                }
            }
            true
        }
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
        val currentTime = formatter.format(time)
        binding.tvStartTime.text = currentTime
    }

    // Save Menu clicker handler where data validation happen
    private fun saveTaskClicked() {
        val title = binding.tvTaskTitle.text.toString().trimMargin()
        val description = binding.tvTaskDescription.text.toString().trimMargin()
        if(title.isEmpty() || description.isEmpty()){
            if (title.isEmpty()){
                showErrorTitle()
            }else if(description.isEmpty()){
                showErrorDescription()
            }else{
                showError()
            }
        }else{
            saveTask(
                Task(
                    id = taskId,
                    title = title,
                    description = description,
                    image = imageuri,
                    startTime = binding.tvStartTime.text.toString().trimMargin(),
                    deadline = binding.tvDeadline.text.toString().trimMargin(),
                    status = false
                )
            )
        }
    }

    // save task to Cloud
    private fun saveTask(taskItem: Task) {
        viewModel.saveTaskToCloud(taskItem)
    }

    // Error for empty title and description
    private fun showError() {
        binding.descriptionLayout.error = "Description is required"
        binding.titleLayout.error = "Title is required"
    }
    private fun showErrorDescription() {
        binding.titleLayout.error = null
        binding.descriptionLayout.error = "Description is required"
    }
    private fun showErrorTitle() {
        binding.titleLayout.error = "Title is required"
        binding.descriptionLayout.error = null
    }

    // Action listener
    private fun initListener() {
        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.tvStartTime.setOnClickListener {
            pickStartDateAndTime()
        }
        binding.tvDeadline.setOnClickListener {
            pickDeadLineDateAndTime()
        }
        binding.topAppBar.setNavigationOnClickListener {
            goBack()
        }
    }

    // Navigate Back
    private fun goBack() {
        findNavController().popBackStack()
    }
    // Image Picker Dialog for pick image
    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Date Time Picker for Start time
    private fun pickStartDateAndTime(){
        var startDate = ""
        val newFragment = DatePickerFragment { yy, mm, dd ->
            val MM = String.format("%02d", mm)
            startDate = "$startDate$dd/$MM/$yy"
            val timeFragment = TimePickerFragment(selectedYear = yy, selectedMonth = mm, selectedDay = dd, onSet = { hour, min ->
                val minute = String.format("%02d", min)
                startDate = "$startDate - $hour : $minute"
                binding.tvStartTime.text = startDate
            })
            timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }
    // Date Time Picker for Deadline
    private fun pickDeadLineDateAndTime(){
        var deadLine = ""
        val newFragment = DatePickerFragment { yy, mm, dd ->
            val MM = String.format("%02d", mm)
            deadLine = "$deadLine$dd/$MM/$yy"
            val timeFragment = TimePickerFragment(selectedYear = yy, selectedMonth = mm, selectedDay = dd, onSet = { hour, min ->
                val minute = String.format("%02d", min)
                deadLine = "$deadLine - $hour : $minute"
                binding.tvDeadline.text = deadLine
            })
            timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }
}