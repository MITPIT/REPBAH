package com.alexherodes.repbah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexherodes.repbah.data.Booking
import com.alexherodes.repbah.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLogout: () -> Unit,
    onScheduleClick: () -> Unit, // 1. Added this parameter
    onBookingClick: (Booking) -> Unit
) {
    val bookings = viewModel.bookings.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Broneeringud", fontWeight = FontWeight.Bold) },
                actions = {
                    // 2. Added Calendar/Schedule Button
                    IconButton(onClick = onScheduleClick) {
                        Icon(Icons.Default.DateRange, contentDescription = "Graafik", tint = Color.White)
                    }

                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logi välja")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color(0xFF69A64E)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(16.dp)
        ) {
            if (bookings.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 50.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Broneeringuid ei ole", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bookings) { booking ->
                        BookingCard(booking = booking, onClick = { onBookingClick(booking) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCard(booking: Booking, onClick: () -> Unit) {
    val statusColor = when (booking.status) {
        "confirmed" -> Color(0xFF69A64E)
        "cancelled" -> Color.Red
        else -> Color.Yellow
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${booking.date} @ ${booking.time}",
                    color = Color(0xFF69A64E),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = booking.status.uppercase(),
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = booking.clientName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = booking.address, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tüüp: ${booking.propertyType} (${booking.details})", color = Color.LightGray, fontSize = 14.sp)
        }
    }
}