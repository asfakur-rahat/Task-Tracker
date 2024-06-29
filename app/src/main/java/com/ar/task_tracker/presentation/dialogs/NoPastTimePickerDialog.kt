package com.ar.task_tracker.presentation.dialogs

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.TimePicker

class NoPastTimePickerDialog(
    context: Context,
    private val isCurrentDate: Boolean,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean,
    onTimeSetListener: OnTimeSetListener
) : TimePickerDialog(context, onTimeSetListener, hourOfDay, minute, is24HourView) {

    private val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (isCurrentDate) {
            if (hourOfDay < currentHour || (hourOfDay == currentHour && minute < currentMinute)) {
                updateTime(currentHour, currentMinute)
            } else {
                super.onTimeChanged(view, hourOfDay, minute)
            }
        } else {
            super.onTimeChanged(view, hourOfDay, minute)
        }
    }
}
