package com.ar.task_tracker.presentation.taskList

import android.os.Bundle
import android.view.View
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet(
    private val onEdit: () -> Unit,
    private val onDetails: () -> Unit,
    private val onCompleted: () -> Unit
) : BottomSheetDialogFragment(R.layout.bottom_sheet_dialog) {

    private lateinit var binding: BottomSheetDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = BottomSheetDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        binding.editBtn.setOnClickListener {
            onEdit()
            dismiss()
        }
        binding.detailsBtn.setOnClickListener {
            onDetails()
            dismiss()
        }
        binding.completedBtn.setOnClickListener {
            onCompleted()
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
