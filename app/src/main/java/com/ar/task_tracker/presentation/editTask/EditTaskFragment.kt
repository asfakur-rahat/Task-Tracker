package com.ar.task_tracker.presentation.editTask

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentAddTaskBinding
import com.ar.task_tracker.presentation.dialogs.DatePickerFragment
import com.ar.task_tracker.presentation.dialogs.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageuri: String? = null
    private val args: EditTaskFragmentArgs by navArgs()

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
        binding = FragmentAddTaskBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.tvDeadline.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        var deadline = ""
        val newFragment = DatePickerFragment { yy, mm, dd ->
            deadline = "$deadline$dd/$mm/$yy"
            val timeFragment = TimePickerFragment(selectedYear = yy, selectedMonth = mm, selectedDay = dd, onSet = { hour, min ->
                val minute = String.format("%02d", min)
                deadline = "$deadline - $hour : $minute"
                binding.tvDeadline.text = deadline
            })
            timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleImage(uri: Uri) {
        imageuri = uri.toString()
        binding.ivTaskImage.setImageURI(uri)
    }
}