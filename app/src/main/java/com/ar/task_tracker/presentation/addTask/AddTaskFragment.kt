package com.ar.task_tracker.presentation.addTask

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentAddTaskBinding
import com.ar.task_tracker.domain.model.Task
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTaskFragment : Fragment(R.layout.fragment_add_task){

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
        Log.d("URI is after getting image", uri.toString())
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableID.collectLatest{
                taskId = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cloudDone.collectLatest{
                if(it){
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
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allDone.collectLatest {
                if (it) {
                    goBack()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loader.collectLatest{
                if(it){
                    showLoader()
                }else{
                    hideLoader()
                }
            }
        }
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
        binding.mainView.visibility = View.GONE
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
        val deadLine = binding.tvDeadline.text.toString().trimMargin()
        if(title.isEmpty() || description.isEmpty()){
            if (title.isEmpty()){
                showErrorTitle()
            }else if(description.isEmpty()){
                showErrorDescription()
            }else{
                showError()
            }
        }else{
            if(deadLine == resources.getString(R.string.deadline_time_date)){
                Toast.makeText(requireContext(), "Please select deadline time", Toast.LENGTH_SHORT).show()
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

    private var tag = "start"
    // Action listener
    private fun initListener() {
        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.tvStartTime.setOnClickListener {
            tag = "start"
            openDialog()
        }
        binding.tvDeadline.setOnClickListener {
            tag = "due"
            openDialog()
        }
        binding.topAppBar.setNavigationOnClickListener {
            goBack()
        }
    }

    private fun openDialog() {
        var title = "Select Deadline Date"
        if(tag =="start"){
            title = "Select Start Date"
        }
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(title).setSelection(
            MaterialDatePicker.todayInUtcMilliseconds()).setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            showTimePicker(it)
        }
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun showTimePicker(selectedDate: Long) {
        var title = "Select Deadline Time"
        if(tag =="start"){
            title = "Select Start Time"
        }
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        val timePicker = MaterialTimePicker.Builder().setTitleText(title).setHour(currentHour).setMinute(currentMinute).setInputMode(
            MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            val selectedDateTime = getSelectedTime(selectedDate, selectedHour, selectedMinute)
            val now = getCurrentTime(calendar)

            if (selectedDateTime.before(now)) {
                Toast.makeText(requireContext(), "Cannot select past time", Toast.LENGTH_SHORT).show()
                showTimePicker(selectedDate)
            } else {
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                val dateTime = "$formattedDate - $selectedTime"
                if(tag =="start"){
                    binding.tvStartTime.text = dateTime
                }else{
                    binding.tvDeadline.text = dateTime
                }
            }
        }
        timePicker.show(requireActivity().supportFragmentManager, "TimePicker")
    }

    private fun getSelectedTime(selectedDate: Long, selectedHour: Int, selectedMinute: Int) = Calendar.getInstance().apply {
        timeInMillis = selectedDate
        set(Calendar.HOUR_OF_DAY, selectedHour)
        set(Calendar.MINUTE, selectedMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun getCurrentTime(calendar: Calendar) = Calendar.getInstance().apply {
        timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, calendar.get(Calendar.SECOND))
        set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND))
    }

    // Navigate Back
    private fun goBack() {
        findNavController().popBackStack()
    }
    // Image Picker Dialog for pick image
    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}