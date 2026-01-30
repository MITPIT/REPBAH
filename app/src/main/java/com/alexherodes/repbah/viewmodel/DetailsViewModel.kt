package com.alexherodes.repbah.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexherodes.repbah.data.Booking
import com.alexherodes.repbah.data.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsViewModel : ViewModel() {
    private val repository = BookingRepository()
    private val _uiState = MutableStateFlow(Booking())
    val uiState: StateFlow<Booking> = _uiState

    fun setBooking(booking: Booking) { _uiState.value = booking }

    // --- DATABASE ACTIONS ---
    fun confirmBooking(context: Context, onSuccess: () -> Unit) {
        val updated = _uiState.value.copy(status = "confirmed")
        save(updated) {
            // Trigger Email Draft
            sendEmailDraft(context, updated, isConfirmation = true)
            onSuccess()
        }
    }

    fun cancelBooking(context: Context, onSuccess: () -> Unit) {
        val updated = _uiState.value.copy(status = "cancelled")
        save(updated) {
            // Trigger Email Draft
            sendEmailDraft(context, updated, isConfirmation = false)
            onSuccess()
        }
    }

    private fun save(booking: Booking, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateBooking(booking)
            onSuccess()
        }
    }

    // --- INTENT HELPERS ---

    fun callPhone(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun sendEmail(context: Context, email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            context.startActivity(intent)
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Automatic Email Drafter
    private fun sendEmailDraft(context: Context, booking: Booking, isConfirmation: Boolean) {
        val subject = if (isConfirmation) "✅ Kinnitus: Teie broneering" else "❌ Tühistamine: Teie broneering"

        val body = if (isConfirmation) {
            """
            Tere ${booking.clientName}!
            
            Kinnitan Teie broneeringu:
            📅 Aeg: ${booking.date} kell ${booking.time}
            📍 Aadress: ${booking.address}
            
            Kohtumiseni!
            Alex Herodes
            """.trimIndent()
        } else {
            """
            Tere ${booking.clientName}.
            
            Kahjuks pean tühistama Teie broneeringu:
            📅 Aeg: ${booking.date} kell ${booking.time}
            
            Vabandame ebamugavuste pärast.
            Alex Herodes
            """.trimIndent()
        }

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${booking.email}")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(intent)
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Google Calendar Logic
    fun addToCalendar(context: Context) {
        val booking = _uiState.value
        val startTime = try {
            val startHour = booking.time.split("-")[0]
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).parse("${booking.date} $startHour")?.time
        } catch (e: Exception) { null }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "📸 Foto: ${booking.clientName}")
            putExtra(CalendarContract.Events.EVENT_LOCATION, booking.address)
            putExtra(CalendarContract.Events.DESCRIPTION, "${booking.propertyType}\n${booking.phone}\n${booking.comments}")
            if (startTime != null) {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + (60 * 60 * 1000))
            }
        }
        context.startActivity(intent)
    }
}