package com.netra.parkirin.officer.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Url

data class ParkingEntryRequest(
    val plateNumber: String,
    val vehicleType: String = "MOTORCYCLE",
    val zoneId: String,
    val streetId: String,
    val streetName: String,
    val entryLat: Double,
    val entryLng: Double,
    val entryAccuracy: Float,
    val entryPhotoUrl: String? = null
)

data class ParkingEntryResponse(
    val sessionId: String,
    val sessionNumber: String,
    val plateNumber: String,
    val vehicleType: String?,
    val zoneId: String,
    val isSubscription: Boolean = false,
    val status: String,
    val entryTime: String,
    val invoiceId: String? = null,
    val lotteryCode: String? = null
)

data class ParkingSessionResponse(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("zoneId", alternate = ["zone_id"])
    val zoneId: String?,
    
    @SerializedName("platenumber", alternate = ["plateNumber", "plate_number"])
    val platenumber: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("entrytime", alternate = ["entryTime", "entry_time"])
    val entrytime: String?,
    
    @SerializedName("exittime", alternate = ["exitTime", "exit_time"])
    val exittime: String?,
    
    @SerializedName("vehicletype", alternate = ["vehicleType", "vehicle_type"])
    val vehicletype: String?,
    
    @SerializedName("totalAmount", alternate = ["total_amount"])
    val totalAmount: Long? = 0,
    
    // UI Helpers (Calculated)
    val durationMinutes: Int = 0,
    val amount: Long = 0
)

data class ParkingExitRequest(
    val plateNumber: String,
    val zoneId: String,
    val paymentMethod: String = "CASH"
)

data class ParkingExitResponse(
    val sessionId: String,
    val invoiceId: String? = null,
    val zoneId: String? = null,
    val plateNumber: String,
    val entryTime: String,
    val exitTime: String,
    val durationMinutes: Int,
    val amount: Long,
    val paymentMethod: String
)
data class CashPaymentRequest(
    val amountReceived: Long
)

data class CashPaymentResponse(
    val receiptNumber: String,
    val lotteryCode: String,
    val amount: Long,
    val amountReceived: Long,
    val change: Long,
    val paidAt: String
)

data class NotifyResponse(
    val sent: Boolean,
    val phone: String?
)

data class DashboardSummaryResponse(
    val totalSessions: Int,
    val activeSessions: Int,
    val totalRevenue: Long,
    val avgDurationMinutes: Int
)

data class VehiclePlateResponse(
    val id: String,
    val plateNumber: String,
    val vehicleType: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerUserId: String,
    val createdAt: String,
    val updatedAt: String
)

interface TransactionApiService {

    @Headers("Connection: close")
    @POST("api/v1/parking/entry")
    suspend fun recordEntry(
        @Header("X-User-Id") userId: String,
        @Body request: ParkingEntryRequest
    ): Response<ParkingEntryResponse>

    @POST("api/v1/parking/exit")
    suspend fun recordExit(
        @Header("X-User-Id") userId: String,
        @Body request: ParkingExitRequest
    ): ParkingExitResponse

    @POST("api/v1/payments/{invoiceId}/cash")
    suspend fun processCashPayment(
        @Header("X-User-Id") userId: String,
        @Path("invoiceId") invoiceId: String,
        @Query("zoneId") zoneId: String, // 🚀 TAMBAHIN INI MEKS! Biar nempel jadi ?zoneId=...
        @Body request: CashPaymentRequest
    ): CashPaymentResponse

    @POST("api/v1/invoices/{invoiceId}/notify")
    suspend fun notifyAbsent(
        @Header("X-User-Id") userId: String,
        @Path("invoiceId") invoiceId: String
    ): NotifyResponse

    @GET("api/v1/parking/sessions/active")
    suspend fun getActiveSessions(
        @Header("X-User-Id") userId: String,
        @Query("zoneId") zoneId: String
    ): List<ParkingSessionResponse>

    @GET("api/v1/parking/sessions")
    suspend fun getParkingSessions(
        @Header("X-User-Id") userId: String,
        @Query("zoneId") zoneId: String,
        @Query("date") date: String
    ): List<ParkingSessionResponse>

    @GET("api/v1/parking/sessions")
    suspend fun getSessions(
        @Header("X-User-Id") userId: String,
        @Query("zoneId") zoneId: String,
        @Query("date") date: String? = null
    ): List<ParkingSessionResponse>

    @GET("api/v1/parking/sessions")
    suspend fun getSessionByPlate(
        @Header("X-User-Id") userId: String,
        @Query("plateNumber") plateNumber: String,
        @Query("zoneId") zoneId: String
    ): List<ParkingSessionResponse>

    @GET("api/v1/parking/summary/today")
    suspend fun getTodaySummary(
        @Header("X-User-Id") userId: String,
        @Query("zoneId") zoneId: String
    ): DashboardSummaryResponse
}
