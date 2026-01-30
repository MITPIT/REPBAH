package com.alexherodes.repbah.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexherodes.repbah.data.Booking
import com.alexherodes.repbah.data.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = BookingRepository()

    // The list of bookings we show on screen
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    init {
        // Start listening for data as soon as this screen opens
        viewModelScope.launch {
            repository.getBookings().collect { list ->
                _bookings.value = list
            }
        }
    }
}