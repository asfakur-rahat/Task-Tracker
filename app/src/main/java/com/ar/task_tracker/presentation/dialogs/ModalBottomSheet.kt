package com.ar.task_tracker.presentation.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet_dialog) {

    private lateinit var binding: BottomSheetDialogBinding
    private var listener: ModalBottomSheetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Try to get the listener from the target fragment or activity context
        listener = when {
            targetFragment is ModalBottomSheetListener -> targetFragment as ModalBottomSheetListener
            context is ModalBottomSheetListener -> context
            else -> null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = BottomSheetDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.editBtn.setOnClickListener {
            listener?.onEdit()
            dismiss()
        }
        binding.detailsBtn.setOnClickListener {
            listener?.onDetails()
            dismiss()
        }
        binding.completedBtn.setOnClickListener {
            listener?.onCompleted()
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dismissAllowingStateLoss()
    }

    companion object {
        const val TAG = "ModalBottomSheet"

        fun newInstance(): ModalBottomSheet {
            return ModalBottomSheet()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
