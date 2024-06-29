package com.ar.task_tracker.presentation.editTask

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentEditTaskBinding
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.presentation.dialogs.DatePickerFragment
import com.ar.task_tracker.presentation.dialogs.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {

    private lateinit var binding: FragmentEditTaskBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: EditTaskViewModel by viewModels()
    private var imageuri: String? = null
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
            }
        }
        binding.tvStartTime.text = args.task.startTime
        binding.tvDeadline.text = args.task.deadline

        val items = listOf("Completed", "Pending")
        val adapter = ArrayAdapter(requireContext(), R.layout.status_item, items)
        val autoCompleteTextView = binding.status.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(adapter)
        status = args.task.status
        val initialStatus = if (args.task.status) "Completed" else "Pending"
        autoCompleteTextView?.setText(initialStatus, false)


        autoCompleteTextView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedStatus = parent.getItemAtPosition(position).toString()
            status = selectedStatus == "Completed"
        }

        binding.ivTaskImage.setOnClickListener {
            pickImage()
        }
        binding.tvDeadline.setOnClickListener {
            openDialog()
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.save -> {
                    saveTask(
                        args.task.copy(
                            title = binding.tvTaskTitle.text.toString().trimMargin(),
                            description = binding.tvTaskDescription.text.toString().trimMargin(),
                            deadline = binding.tvDeadline.text.toString().trimMargin(),
                            status = status
                        )
                    )
                }
            }
            true
        }
    }


    private fun initObservers() {
        viewModel.cloudDone.observe(viewLifecycleOwner){
            if (it == true){
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
        viewModel.allDone.observe(viewLifecycleOwner){
            if(it==true){
                findNavController().popBackStack()
            }
        }
        viewModel.loader.observe(viewLifecycleOwner){
            if (it ==true){
                binding.mainView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.mainView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun saveTask(task: Task) {
        viewModel.saveTaskToCloud(task,imageuri)
    }

    private fun openDialog() {
        var deadline = ""
        val newFragment = DatePickerFragment { yy, mm, dd ->
            val MM = String.format("%02d", mm)
            deadline = "$deadline$dd/$MM/$yy"
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