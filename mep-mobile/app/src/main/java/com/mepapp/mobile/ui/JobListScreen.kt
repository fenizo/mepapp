package com.mepapp.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MobileJob(val id: String, val customer: String, val service: String, val status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(onJobClick: (String) -> Unit) {
    val jobs = listOf(
        MobileJob("1", "John Doe", "Electrical Repair", "In Progress"),
        MobileJob("2", "Sarah Khan", "AC Fix", "Pending"),
        MobileJob("3", "Mike Ross", "Pipe Leak", "Pending")
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Assigned Jobs", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs) { job ->
                Card(
                    onClick = { onJobClick(job.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(job.customer, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            StatusBadge(job.status)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(job.service, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = if (status == "In Progress") Color(0xFF38BDF8) else Color(0xFFF59E0B)
    Box(
        modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
