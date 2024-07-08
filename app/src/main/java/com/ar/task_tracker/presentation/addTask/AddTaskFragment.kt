package com.ar.task_tracker.presentation.addTask

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentAddTaskBinding
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.presentation.addTask.dialog.BottomSheet
import com.ar.task_tracker.presentation.addTask.dialog.SheetListener
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.lifecycle.ProcessCameraProvider
import com.ar.task_tracker.presentation.addTask.notification.scheduleTaskReminder


@AndroidEntryPoint
class AddTaskFragment : Fragment(R.layout.fragment_add_task), SheetListener{

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: AddTaskViewModel by viewModels()
    private var imageuri: String? = null
    private var taskId: Int = 0

    private var deadline: Long = Calendar.getInstance().timeInMillis


    private var imageCapture: ImageCapture? = null
    private lateinit var executor: ExecutorService
    private var isUsingBackCamera = true
    private lateinit var cameraProvider: ProcessCameraProvider
    private var savedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
                handleImage(uri)
            }
        }

        if(!allPermissionsGranted()){
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }



        executor = Executors.newSingleThreadExecutor()
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
        setNotification()
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
            showOptions()
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

        binding.btnRetake.setOnClickListener {
            retakePhoto()
        }
        binding.btnConfirm.setOnClickListener {
            confirmPhoto()
        }

        binding.camRotate.setOnClickListener {
            toggleCamera()
        }
        binding.camButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun showOptions() {
        val bottomSheet = BottomSheet.newInstance()
        bottomSheet.setTargetFragment(this, 0)
        bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
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
            if(tag =="due"){
                deadline = getSelectedTime(selectedDate,selectedHour,selectedMinute).timeInMillis
            }
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

    private fun setNotification(){
        scheduleTaskReminder(requireContext(),taskId, binding.tvTaskTitle.text.toString(), deadline)
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

    override fun onCamera() {
        if(!allPermissionsGranted()){
            requestPermissions()
        }else{
            binding.mainView.visibility = View.GONE
            binding.captureLayout.visibility = View.VISIBLE
            startCamera()
        }
    }

    override fun onGallery() {
        pickImage()
    }

    private fun toggleCamera() {
        isUsingBackCamera = !isUsingBackCamera
        bindCameraUseCase()
    }
    private fun bindCameraUseCase() {
        val cameraSelector = if (isUsingBackCamera) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        val preview = androidx.camera.core.Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.camPreview.surfaceProvider)
            }
        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value)
                permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(requireActivity().baseContext, "Permission request denied", Toast.LENGTH_SHORT).show()
        } else {
            startCamera()
        }
    }



    private fun retakePhoto() {
        savedImageUri?.let { uri ->
            requireActivity().contentResolver.delete(uri, null, null)
            savedImageUri = null
        }
        startCamera()
        binding.previewLayout.visibility = View.GONE
        binding.captureLayout.visibility = View.VISIBLE
    }
    private fun confirmPhoto() {
        Toast.makeText(requireContext(), "Photo saved: $savedImageUri", Toast.LENGTH_SHORT).show()
        stopCameraPreview()
        if (savedImageUri != null) {
            handleImage(savedImageUri!!)
        }
        binding.previewLayout.visibility = View.GONE
        binding.captureLayout.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireActivity().contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext().applicationContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedImageUri = output.savedUri
                    Log.d("saved override", "Photo capture succeeded: ${output.savedUri}")
                    showImagePreview()
                }
            }
        )
    }
    private fun showImagePreview() {
        val uri = Uri.parse(savedImageUri.toString())
        binding.previewLayout.visibility = View.VISIBLE
        binding.capturedImage.setImageURI(uri)
        stopCameraPreview()
        binding.captureLayout.visibility = View.GONE
        binding.mainView.visibility = View.GONE
    }
    private fun stopCameraPreview() {
        try {
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping camera preview", e)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCase()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                CAMERA,
                READ_MEDIA_IMAGES
            ).toTypedArray()
        } else {
            mutableListOf(
                CAMERA,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            ).toTypedArray()
        }
    }

}