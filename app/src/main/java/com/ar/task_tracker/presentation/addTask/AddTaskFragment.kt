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
        viewModel.availableID.observe(viewLifecycleOwner) {
            taskId = it
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
            if (it == true) {
                findNavController().popBackStack()
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
            findNavController().popBackStack()
        }
    }

    private fun saveTaskClicked() {
        val title = binding.tvTaskTitle.text.toString().trimMargin()
        val description = binding.tvTaskDescription.text.toString().trimMargin()
        if(title.isEmpty() || description.isEmpty()){
            if (title.isEmpty()){
                binding.titleLayout.error = "Title is required"
                binding.descriptionLayout.error = null
            }else{
                binding.titleLayout.error = null
                binding.descriptionLayout.error = "Description is required"
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