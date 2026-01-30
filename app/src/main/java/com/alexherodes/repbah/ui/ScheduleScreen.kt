package com.alexherodes.repbah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexherodes.repbah.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit
) {
    val selectedDate = viewModel.selectedDate.collectAsState().value
    val blockedState = viewModel.blockedState.collectAsState().value

    // For the Date Picker Dialog
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Töögraafik") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tagasi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. DATE SELECTOR
            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF69A64E))
                Spacer(modifier = Modifier.width(16.dp))
                Text(selectedDate, fontSize = 20.sp, color = Color.White)
            }

            Divider(color = Color.DarkGray)

            // 2. FULL DAY TOGGLE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleFullDay() }
            ) {
                Checkbox(
                    checked = blockedState.fullDay,
                    onCheckedChange = { viewModel.toggleFullDay() },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Red, uncheckedColor = Color.Gray)
                )
                Text(
                    "MÄRGI Terve PÄEV VABAKS",
                    color = if(blockedState.fullDay) Color.Red else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. TIME SLOTS
            if (!blockedState.fullDay) {
                Text("Või blokeeri kellaajad:", color = Color.Gray)

                val allTimes = listOf("10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00")

                allTimes.forEach { time ->
                    val isBlocked = blockedState.times.contains(time)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.toggleTimeSlot(time) }
                            .background(if (isBlocked) Color(0xFF330000) else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isBlocked,
                            onCheckedChange = { viewModel.toggleTimeSlot(time) },
                            colors = CheckboxDefaults.colors(checkedColor = Color.Red, uncheckedColor = Color.Gray)
                        )
                        Text(
                            time,
                            color = if (isBlocked) Color.Red else Color.White,
                            fontSize = 18.sp
                        )
                        if (isBlocked) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text("BLOKEERITUD", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Text(
                    "⚠️ Kogu päev on märgitud puhkepäevaks. Kliendid ei saa selleks päevaks aegu broneerida.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}