package com.alexherodes.repbah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexherodes.repbah.viewmodel.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBack: () -> Unit
) {
    val booking = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Broneeringu Info") },
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
            // 1. HEADER (Date & Time)
            Column {
                Text("AEG", color = Color(0xFF69A64E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${booking.date}  |  ${booking.time}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Divider(color = Color.DarkGray)

            // 2. CONTACT INFO (Clickable)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("KONTAKT", color = Color(0xFF69A64E), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // Name (Read Only)
                InfoRow(icon = Icons.Default.Person, text = booking.clientName)

                // Phone (Click to Call)
                InfoRow(
                    icon = Icons.Default.Call,
                    text = booking.phone,
                    isLink = true,
                    onClick = { viewModel.callPhone(context, booking.phone) }
                )

                // Email (Click to Mail)
                InfoRow(
                    icon = Icons.Default.Email,
                    text = booking.email,
                    isLink = true,
                    onClick = { viewModel.sendEmail(context, booking.email) }
                )
            }

            Divider(color = Color.DarkGray)

            // 3. PROPERTY DETAILS
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("OBJEKTI INFO", color = Color(0xFF69A64E), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                InfoRow(icon = Icons.Default.Home, text = booking.address)
                InfoRow(icon = Icons.Default.Info, text = "${booking.propertyType.uppercase()} — ${booking.details}")
            }

            Divider(color = Color.DarkGray)

            // 4. COMMENTS (Read Only)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MÄRKUSED", color = Color(0xFF69A64E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    text = if (booking.comments.isBlank()) "Puuduvad" else booking.comments,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. ACTION BUTTONS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69A64E)),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("KINNITA")
                }

                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("TÜHISTA")
                }
            }

            // Calendar Button
            Button(
                onClick = { viewModel.addToCalendar(context) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LISA KALENDRISSE", color = Color.White)
            }
        }

        // --- DIALOGS ---
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Kinnita ja Saada E-mail?") },
                text = { Text("See märgib broneeringu kinnitatuks ja avab Sinu e-maili rakenduse valmis vastusega.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.confirmBooking(context, onSuccess = onBack)
                        showConfirmDialog = false
                    }) { Text("KINNITA", color = Color(0xFF69A64E)) }
                },
                dismissButton = { TextButton(onClick = { showConfirmDialog = false }) { Text("LOOBU") } }
            )
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Tühista ja Saada E-mail?") },
                text = { Text("See tühistab broneeringu ja avab e-maili rakenduse teavitusega.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.cancelBooking(context, onSuccess = onBack)
                        showCancelDialog = false
                    }) { Text("TÜHISTA", color = Color.Red) }
                },
                dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text("LOOBU") } }
            )
        }
    }
}

// Helper Component for Rows
@Composable
fun InfoRow(icon: ImageVector, text: String, isLink: Boolean = false, onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isLink) Modifier.clickable { onClick?.invoke() } else Modifier)
    ) {
        Icon(icon, contentDescription = null, tint = if (isLink) Color(0xFF69A64E) else Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = if (isLink) Color(0xFF69A64E) else Color.White,
            fontSize = 18.sp,
            fontWeight = if (isLink) FontWeight.Bold else FontWeight.Normal
        )
    }
}