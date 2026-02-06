package com.mepapp.mobile.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.mepapp.mobile.MainActivity

@Composable
fun MainNavigation() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val authRepository = remember { com.mepapp.mobile.data.AuthRepository(context) }
    val tokenState = authRepository.authToken.collectAsState(initial = null)
    val userIdState = authRepository.userId.collectAsState(initial = null)

    var currentScreen by remember { mutableStateOf("login") }
    var selectedJobId by remember { mutableStateOf("") }
    var selectedBookingId by remember { mutableStateOf(-1) }

    // Navigation logic based on authentication
    val startScreen = if (tokenState.value != null) "list" else "login"

    LaunchedEffect(tokenState.value) {
        if (tokenState.value != null) {
            com.mepapp.mobile.network.NetworkModule.setAuthToken(tokenState.value!!)
            if (currentScreen == "login") {
                currentScreen = "list"
            }
        }
    }

    // Handle deep link from booking notification
    LaunchedEffect(Unit) {
        val pendingId = MainActivity.pendingBookingId
        if (pendingId != null && pendingId > 0) {
            selectedBookingId = pendingId
            currentScreen = "booking_detail"
            MainActivity.pendingBookingId = null
        }
    }

    val workManager = androidx.work.WorkManager.getInstance(context)
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("CallLogSync")
        .observeAsState(initial = emptyList())

    val isSyncing = workInfos.value.any { it.state == androidx.work.WorkInfo.State.RUNNING }

    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        when (currentScreen) {
            "login" -> LoginScreen(authRepository, onLoginSuccess = {
                currentScreen = "list"
            })
            "list" -> JobListScreen(
                userId = userIdState.value,
                token = tokenState.value,
                onJobClick = { id ->
                    selectedJobId = id
                    currentScreen = "details"
                },
                onLogsClick = {
                    currentScreen = "logs"
                },
                onBookingsClick = {
                    currentScreen = "bookings"
                }
            )
            "details" -> JobDetailScreen(jobId = selectedJobId, onBack = {
                currentScreen = "list"
            })
            "logs" -> CallLogsScreen(onBack = {
                currentScreen = "list"
            })
            "bookings" -> BookingListScreen(
                onBookingClick = { bookingWpId ->
                    selectedBookingId = bookingWpId
                    currentScreen = "booking_detail"
                },
                onBack = {
                    currentScreen = "list"
                }
            )
            "booking_detail" -> BookingDetailScreen(
                bookingWpId = selectedBookingId,
                onBack = {
                    currentScreen = "bookings"
                }
            )
        }

        if (isSyncing) {
            Surface(
                color = androidx.compose.ui.graphics.Color(0xFF38BDF8), // Light Blue
                modifier = androidx.compose.ui.Modifier
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                androidx.compose.material3.Text(
                    text = "Syncing Call Logs...",
                    color = androidx.compose.ui.graphics.Color.Black,
                    modifier = androidx.compose.ui.Modifier.padding(8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
