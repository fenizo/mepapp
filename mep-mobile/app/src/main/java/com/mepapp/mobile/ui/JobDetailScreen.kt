package com.mepapp.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(jobId: String, onBack: () -> Unit) {
    var status by remember { mutableStateOf("In Progress") }
    var qrVerified by remember { mutableStateOf(false) }
    var materialCharge by remember { mutableStateOf("") }
    var serviceCharge by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = remember { com.mepapp.mobile.network.NetworkModule.createService<com.mepapp.mobile.network.MepApiService>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details: #$jobId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Customer: John Doe", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                            Text("Address: 123 Tech Park, Bangalore", color = Color.Gray)
                            Text("Service: Electrical Repair", color = Color.Gray)
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    try {
                                        // 1. Launch Dialer
                                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                            data = android.net.Uri.parse("tel:9876543210")
                                        }
                                        context.startActivity(intent)

                                        // 2. Log call to backend
                                        apiService.logCall(com.mepapp.mobile.network.CallLogRequest(jobId, "STAFF_ID"))
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Failed to log call", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF38BDF8))
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Customer",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            if (!qrVerified) {
                Button(
                    onClick = { qrVerified = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Scan Google Review QR")
                }
                Text("Scan QR to unlock 10% discount for customer", fontSize = 12.sp, color = Color.Gray)
            } else {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f))) {
                    Text("✅ Google Review QR Verified! 10% Discount Applied.", modifier = Modifier.padding(16.dp), color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = materialCharge,
                onValueChange = { materialCharge = it },
                label = { Text("Material Charges (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            OutlinedTextField(
                value = serviceCharge,
                onValueChange = { serviceCharge = it },
                label = { Text("Service Charges (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    scope.launch {
                        try {
                            val material = materialCharge.toDoubleOrNull() ?: 0.0
                            val service = serviceCharge.toDoubleOrNull() ?: 0.0
                            val response = apiService.generateInvoice(jobId, material, service)
                            
                            android.widget.Toast.makeText(context, "Bill Generated: ${response.invoiceNumber} (₹${response.finalAmount})", android.widget.Toast.LENGTH_LONG).show()
                            onBack()
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && serviceCharge.isNotBlank() && materialCharge.isNotBlank()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Complete Job & Generate Bill")
                }
            }
        }
    }
}
