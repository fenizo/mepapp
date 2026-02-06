package com.mepapp.mobile.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mepapp.mobile.database.AppDatabase
import com.mepapp.mobile.database.BookingEntity
import com.mepapp.mobile.network.UpdateBookingRequest
import com.mepapp.mobile.network.WordPressApiModule
import com.mepapp.mobile.network.WordPressApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(bookingWpId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val wpApiService = remember { WordPressApiModule.createService<WordPressApiService>() }
    var booking by remember { mutableStateOf<BookingEntity?>(null) }
    var notes by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(bookingWpId) {
        booking = database.bookingDao().getBookingByWpId(bookingWpId)
        notes = booking?.notes ?: ""
        isLoading = false
    }

    fun updateStatus(newStatus: String) {
        scope.launch {
            isUpdating = true
            try {
                // Try API first
                wpApiService.updateBooking(
                    bookingWpId,
                    UpdateBookingRequest(status = newStatus, notes = notes.ifBlank { null })
                )
                // Update local DB without pending sync (already synced)
                database.bookingDao().updateBookingStatus(
                    wpId = bookingWpId,
                    status = newStatus,
                    notes = notes.ifBlank { null },
                    pendingSync = null
                )
                Toast.makeText(context, "Booking ${newStatus}!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("BookingDetail", "API update failed, saving locally", e)
                // Save locally with pending sync flag
                database.bookingDao().updateBookingStatus(
                    wpId = bookingWpId,
                    status = newStatus,
                    notes = notes.ifBlank { null },
                    pendingSync = newStatus
                )
                Toast.makeText(context, "Saved offline - will sync when online", Toast.LENGTH_SHORT).show()
            }
            booking = database.bookingDao().getBookingByWpId(bookingWpId)
            isUpdating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp, color = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF38BDF8))
            }
        } else if (booking == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Booking not found", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            val b = booking!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                val statusColor = when (b.status) {
                    "pending" -> Color(0xFFF97316)
                    "confirmed" -> Color(0xFF38BDF8)
                    "completed" -> Color(0xFF10B981)
                    "cancelled" -> Color(0xFFEF4444)
                    else -> Color.Gray
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ${b.status.uppercase()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        if (b.pendingSyncStatus != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(offline)",
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                // Customer Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Customer Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        DetailRow(label = "Name", value = b.customerName)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Phone with call button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Phone", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(b.mobileNumber, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${b.mobileNumber}")
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("📞 Call", color = Color.White)
                            }
                        }
                    }
                }

                // Booking Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Booking Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        DetailRow(label = "Service", value = b.serviceType)
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow(label = "Date & Time", value = b.bookingTime)

                        if (b.latitude != null && b.longitude != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Location", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text(
                                        "${b.latitude}, ${b.longitude}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Button(
                                    onClick = {
                                        val gmmIntentUri = Uri.parse("geo:${b.latitude},${b.longitude}?q=${b.latitude},${b.longitude}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            // Fallback to browser
                                            val browserIntent = Intent(Intent.ACTION_VIEW,
                                                Uri.parse("https://www.google.com/maps?q=${b.latitude},${b.longitude}"))
                                            context.startActivity(browserIntent)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("📍 Map", color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Notes Input
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Notes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Add notes...", color = Color(0xFF94A3B8)) },
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                    }
                }

                // Action Buttons
                if (b.status == "pending") {
                    Button(
                        onClick = { updateStatus("confirmed") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Accept Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                if (b.status == "confirmed") {
                    Button(
                        onClick = { updateStatus("completed") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Mark Completed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                if (b.status == "pending" || b.status == "confirmed") {
                    OutlinedButton(
                        onClick = { updateStatus("cancelled") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isUpdating,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF4444))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFEF4444))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
    }
}
