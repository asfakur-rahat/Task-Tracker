package com.ar.task_tracker.presentation.addTask.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet: BottomSheetDialogFragment(R.layout.bottom_sheet) {

    private lateinit var binding: BottomSheetBinding
    private var listener: SheetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when{
            targetFragment is SheetListener -> targetFragment as SheetListener
            context is SheetListener -> context
            else -> null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = BottomSheetBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.fromCamera.setOnClickListener {
            listener?.onCamera()
            dismiss()
        }
        binding.fromGallery.setOnClickListener {
            listener?.onGallery()
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dismissAllowingStateLoss()
    }

    companion object {
        const val TAG = "BottomSheet"

        fun newInstance(): BottomSheet {
            return BottomSheet()
        }
    }
}