package com.mepapp.mobile.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface WordPressApiService {

    @GET("wp-json/mepstep/v1/bookings")
    suspend fun getBookings(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 200,
        @Query("offset") offset: Int = 0
    ): BookingsResponse

    @PATCH("wp-json/mepstep/v1/bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: Int,
        @Body request: UpdateBookingRequest
    ): UpdateBookingResponse
}

data class BookingsResponse(
    val success: Boolean,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val items: List<BookingItem>
)

data class BookingItem(
    val id: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("mobile_number") val mobileNumber: String,
    @SerializedName("service_type") val serviceType: String,
    val latitude: String?,
    val longitude: String?,
    @SerializedName("location_accuracy") val locationAccuracy: String?,
    @SerializedName("booking_time") val bookingTime: String,
    val status: String,
    val notes: String?
)

data class UpdateBookingRequest(
    val status: String,
    val notes: String? = null
)

data class UpdateBookingResponse(
    val success: Boolean,
    val item: BookingItem?
)
