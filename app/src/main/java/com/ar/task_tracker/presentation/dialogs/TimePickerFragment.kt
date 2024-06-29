package com.ar.task_tracker.presentation.dialogs

import android.app.TimePickerDialog
import android.widget.TimePicker
import android.app.Dialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment(
    private val selectedYear: Int,
    private val selectedMonth: Int,
    private val selectedDay: Int,
    private val onSet: (Int, Int) -> Unit,
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Check if the selected date is the current date
        val isCurrentDate = isCurrentDate(selectedYear, selectedMonth, selectedDay)

        return NoPastTimePickerDialog(
            requireContext(),
            isCurrentDate,
            hour,
            minute,
            false,
            this
        )
    }

    private fun isCurrentDate(year: Int, month: Int, day: Int): Boolean {
        val c = Calendar.getInstance()
        return year == c.get(Calendar.YEAR) && month == c.get(Calendar.MONTH) && day == c.get(Calendar.DAY_OF_MONTH)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        onSet(hourOfDay, minute)
    }
}
