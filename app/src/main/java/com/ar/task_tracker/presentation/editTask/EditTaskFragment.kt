package com.ar.task_tracker.presentation.editTask

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.size.Scale
import coil.size.ViewSizeResolver
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentEditTaskBinding
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
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task){

    private lateinit var binding: FragmentEditTaskBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: EditTaskViewModel by viewModels()
    private var imageuri: String? = null
    private var imageURL: String? = null
    private val args: EditTaskFragmentArgs by navArgs()
    private var status = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEditTaskBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObservers()
    }
    private fun initView() {
        binding.tvTaskTitle.setText(args.task.title)
        binding.tvTaskDescription.setText(args.task.description)
        if(args.task.image == null){
            binding.ivTaskImage.setImageResource(R.drawable.add_image_from_device)
        }else{
            binding.ivTaskImage.load(args.task.image){
                placeholder(R.drawable.image_placeholder)
                size(ViewSizeResolver(binding.ivTaskImage))
                scale(Scale.FILL)
            }
        }
        binding.tvStartTime.text = args.task.startTime
        binding.tvDeadline.text = args.task.deadline
        initStatus()
    }

    // Initialize the Status dropdown menu for user to choose
    private fun initStatus() {
        val items = listOf("Completed", "Pending")
        val adapter = ArrayAdapter(requireContext(), R.layout.status_item, items)
        val autoCompleteTextView = binding.status.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(adapter)
        status = args.task.status
        val initialStatus = if (args.task.status) "Completed" else "Pending"
        autoCompleteTextView?.setText(initialStatus, false)
        autoCompleteTextView?.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedStatus = parent.getItemAtPosition(position).toString()
            status = selectedStatus == "Completed"
        }
    }

    // Listener for all button press etc
    private fun initListener() {
        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.tvDeadline.setOnClickListener {
            pickDeadline()
        }
        binding.topAppBar.setNavigationOnClickListener {
            goBack()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.save -> {
                    saveTaskClicked()
                }
            }
            true
        }
    }

    // Listener Actions
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
            updateTask(args.task.copy(
                    title = title,
                    description = description,
                    deadline = binding.tvDeadline.text.toString().trimMargin(),
                    status = status
            ))
        }
    }

    //Save task to the Cloud
    private fun updateTask(task: Task) {
        viewModel.updateTaskToCloud(task,imageuri, imageURL)
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

    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun pickDeadline() {
        openDialog()
    }
    private fun goBack() {
        findNavController().popBackStack()
    }

    // Handled Picked image by user
    private fun handleImage(uri: Uri) {
        imageuri = uri.toString()
        binding.ivTaskImage.setImageURI(uri)
    }

    // Open Date Time picker dialog for user
    private fun openDialog() {

        val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Deadline Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            showTimePicker(it)
        }
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun showTimePicker(selectedDate: Long) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        val timePicker = MaterialTimePicker.Builder().setTitleText("Select Deadline Time").setHour(currentHour).setMinute(currentMinute).setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            val selectedDateTime = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val now = Calendar.getInstance().apply {
                timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
                set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, calendar.get(Calendar.SECOND))
                set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND))
            }

            if (selectedDateTime.before(now)) {
                Toast.makeText(requireContext(), "Cannot select past time", Toast.LENGTH_SHORT).show()
                showTimePicker(selectedDate)
            } else {
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                val dateTime = "$formattedDate - $selectedTime"
                binding.tvDeadline.text = dateTime
            }
        }
        timePicker.show(requireActivity().supportFragmentManager, "TimePicker")
    }

    // Observer for LiveData
    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cloudDone.collectLatest{
                if (it){
                    viewModel.fetchTaskFromCloud(
                        args.task.id,
                        imageuri,
                        args.task.copy(
                            title = binding.tvTaskTitle.text.toString().trimMargin(),
                            description = binding.tvTaskDescription.text.toString().trimMargin(),
                            deadline = binding.tvDeadline.text.toString().trimMargin(),
                            status = status
                        )
                    )
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentImage.collectLatest{
                if(it != null){
                    imageURL = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allDone.collectLatest{
                if(it){
                    goBack()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loader.collectLatest{
                if (it){
                    showLoader()
                }else{
                    hideLoader()
                }
            }
        }

    }

    // for show and hide loader
    private fun showLoader(){
        binding.progressBar.visibility = View.VISIBLE
        binding.mainView.visibility = View.GONE
    }
    private fun hideLoader(){
        binding.progressBar.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }


}