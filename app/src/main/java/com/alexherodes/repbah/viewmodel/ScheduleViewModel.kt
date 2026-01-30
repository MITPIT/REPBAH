package com.alexherodes.repbah.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexherodes.repbah.data.BlockedDay
import com.alexherodes.repbah.data.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleViewModel : ViewModel() {
    private val repository = BookingRepository()

    private val _selectedDate = MutableStateFlow(getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate

    private val _blockedState = MutableStateFlow(BlockedDay())
    val blockedState: StateFlow<BlockedDay> = _blockedState

    init {
        loadDate(_selectedDate.value)
    }

    fun onDateSelected(millis: Long?) {
        if (millis != null) {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val dateStr = sdf.format(Date(millis))
            _selectedDate.value = dateStr
            loadDate(dateStr)
        }
    }

    private fun loadDate(date: String) {
        viewModelScope.launch {
            // Ensure the state always has the correct date, even if DB load fails
            val existing = repository.getBlockedDay(date)
            _blockedState.value = existing ?: BlockedDay(date = date)
        }
    }

    fun toggleFullDay() {
        val current = _blockedState.value
        // Safety check: Don't save if date is missing
        if (current.date.isEmpty()) return

        val newState = current.copy(fullDay = !current.fullDay)
        _blockedState.value = newState
        saveSafe(newState)
    }

    fun toggleTimeSlot(time: String) {
        val current = _blockedState.value
        if (current.date.isEmpty()) return

        val currentList = current.times.toMutableList()
        if (currentList.contains(time)) {
            currentList.remove(time)
        } else {
            currentList.add(time)
        }

        val newState = current.copy(times = currentList)
        _blockedState.value = newState
        saveSafe(newState)
    }

    // NEW: Safe save function to prevent crashes
    private fun saveSafe(blockedDay: BlockedDay) {
        viewModelScope.launch {
            try {
                repository.setBlockedDay(blockedDay)
            } catch (e: Exception) {
                e.printStackTrace() // Logs error instead of crashing
            }
        }
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }
}