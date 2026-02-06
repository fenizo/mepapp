package com.mepapp.mobile.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    indices = [Index(value = ["wpBookingId"], unique = true)]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wpBookingId: Int,
    val customerName: String,
    val mobileNumber: String,
    val serviceType: String,
    val latitude: String?,
    val longitude: String?,
    val locationAccuracy: String?,
    val bookingTime: String,
    val status: String,
    val notes: String?,
    val isNotified: Boolean = false,
    val pendingSyncStatus: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
