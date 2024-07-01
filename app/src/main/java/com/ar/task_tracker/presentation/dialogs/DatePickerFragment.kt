package com.ar.task_tracker.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment() {

    private var listener: DatePickerListener? = null

    companion object {
        fun newInstance(listener: DatePickerListener): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker =  DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, dayOfMonth ->
            listener?.onDateSet(selectedYear, selectedMonth+1, dayOfMonth)
        }, year, month, day)

        datePicker.datePicker.minDate = c.timeInMillis
        return datePicker
    }
}