package com.mepapp.mobile.database

import androidx.room.*

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBooking(booking: BookingEntity): Long

    @Query("SELECT * FROM bookings WHERE wpBookingId = :wpId LIMIT 1")
    suspend fun getBookingByWpId(wpId: Int): BookingEntity?

    @Query("SELECT * FROM bookings WHERE status = 'pending' AND isNotified = 0")
    suspend fun getUnnotifiedPendingBookings(): List<BookingEntity>

    @Query("UPDATE bookings SET isNotified = 1 WHERE id IN (:ids)")
    suspend fun markAsNotified(ids: List<Long>)

    @Query("UPDATE bookings SET status = :status, notes = :notes, pendingSyncStatus = :pendingSync, lastUpdated = :timestamp WHERE wpBookingId = :wpId")
    suspend fun updateBookingStatus(
        wpId: Int,
        status: String,
        notes: String?,
        pendingSync: String? = null,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("SELECT * FROM bookings ORDER BY bookingTime DESC")
    suspend fun getAllBookings(): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE status = 'pending' ORDER BY bookingTime DESC")
    suspend fun getPendingBookings(): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE pendingSyncStatus IS NOT NULL")
    suspend fun getBookingsWithPendingSync(): List<BookingEntity>

    @Query("UPDATE bookings SET pendingSyncStatus = NULL WHERE wpBookingId = :wpId")
    suspend fun clearPendingSync(wpId: Int)
}
