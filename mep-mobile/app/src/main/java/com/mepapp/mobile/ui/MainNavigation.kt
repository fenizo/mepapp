package com.mepapp.mobile.ui

import androidx.compose.runtime.*

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf("list") }
    var selectedJobId by remember { mutableStateOf("") }

    when (currentScreen) {
        "list" -> JobListScreen(onJobClick = { id ->
            selectedJobId = id
            currentScreen = "details"
        })
        "details" -> JobDetailScreen(jobId = selectedJobId, onBack = {
            currentScreen = "list"
        })
    }
}
