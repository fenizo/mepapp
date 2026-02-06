package com.mepapp.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mepapp.mobile.network.WordPressApiModule
import com.mepapp.mobile.network.WordPressApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(onBookingClick: (Int) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val wpApiService = remember { WordPressApiModule.createService<WordPressApiService>() }
    var bookings by remember { mutableStateOf<List<BookingEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadBookings() {
        scope.launch {
            bookings = database.bookingDao().getAllBookings()
            isLoading = false
        }
    }

    fun refreshFromApi() {
        scope.launch {
            isRefreshing = true
            try {
                val response = wpApiService.getBookings()
                if (response.success) {
                    val bookingDao = database.bookingDao()
                    for (item in response.items) {
                        val wpId = item.id.toIntOrNull() ?: continue
                        val existing = bookingDao.getBookingByWpId(wpId)
                        val entity = com.mepapp.mobile.database.BookingEntity(
                            id = existing?.id ?: 0,
                            wpBookingId = wpId,
                            customerName = item.customerName,
                            mobileNumber = item.mobileNumber,
                            serviceType = item.serviceType,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            locationAccuracy = item.locationAccuracy,
                            bookingTime = item.bookingTime,
                            status = item.status,
                            notes = item.notes,
                            isNotified = existing?.isNotified ?: true,
                            pendingSyncStatus = existing?.pendingSyncStatus,
                            lastUpdated = System.currentTimeMillis()
                        )
                        bookingDao.upsertBooking(entity)
                    }
                    bookings = bookingDao.getAllBookings()
                }
            } catch (e: Exception) {
                // Fall back to local data
            }
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        loadBookings()
        // Also try to refresh from API
        refreshFromApi()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookings", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp, color = Color.Black)
                    }
                },
                actions = {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 12.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF38BDF8)
                        )
                    } else {
                        IconButton(onClick = { refreshFromApi() }) {
                            Text("🔄", fontSize = 18.sp)
                        }
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
        } else if (bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No bookings found", fontSize = 16.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bookings, key = { it.wpBookingId }) { booking ->
                    BookingCard(booking = booking, onClick = { onBookingClick(booking.wpBookingId) })
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingEntity, onClick: () -> Unit) {
    val statusColor = when (booking.status) {
        "pending" -> Color(0xFFF97316) // Orange
        "confirmed" -> Color(0xFF38BDF8) // Blue
        "completed" -> Color(0xFF10B981) // Green
        "cancelled" -> Color(0xFFEF4444) // Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Customer name + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.customerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service type
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔧 ", fontSize = 14.sp)
                Text(
                    text = booking.serviceType,
                    fontSize = 14.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Phone number
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📞 ", fontSize = 14.sp)
                Text(
                    text = booking.mobileNumber,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Booking time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🕐 ", fontSize = 14.sp)
                Text(
                    text = booking.bookingTime,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
