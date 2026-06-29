package com.netra.parkirin.officer.feature.entry.data

import android.util.Log
import com.netra.parkirin.officer.core.network.ParkingEntryRequest
import com.netra.parkirin.officer.core.network.ParkingEntryResponse
import com.netra.parkirin.officer.core.network.TransactionApiService
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val transactionApiService: TransactionApiService
) {
    suspend fun recordEntry(
        userId: String,
        plateNumber: String,
        zoneId: String,
        streetId: String,
        streetName: String,
        lat: Double,
        lng: Double,
        accuracy: Float,
        photoBase64: String? = null,
        vehicleType: String
    ): Result<ParkingEntryResponse> {
        var lastException: Exception? = null
        val maxRetries = 2

        for (attempt in 0..maxRetries) {
            try {
                if (attempt > 0) {
                    Log.d("PARKIRIN_REPO", "Retrying recordEntry, attempt $attempt...")
                    delay(500L * attempt) // Simple backoff
                }

                val response = transactionApiService.recordEntry(
                    userId = userId,
                    request = ParkingEntryRequest(
                        plateNumber = plateNumber,
                        zoneId = zoneId,
                        streetId = streetId,
                        streetName = streetName,
                        entryLat = lat,
                        entryLng = lng,
                        entryAccuracy = accuracy,
                        entryPhotoUrl = photoBase64
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    return if (body != null) {
                        Log.d("PARKIRIN_REPO", "Record Entry Berhasil: $body")
                        Result.success(body)
                    } else {
                        Log.e("PARKIRIN_REPO", "Server sukses tapi body kosong")
                        Result.failure(Exception("Response body kosong"))
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        "Gagal membaca error body: ${e.message}"
                    }
                    Log.e("PARKIRIN_REPO", "Server Error: ${response.code()} | $errorBody")
                    return Result.failure(Exception("Server error (${response.code()}): $errorBody"))
                }
            } catch (e: IOException) {
                Log.w("PARKIRIN_REPO", "Network Error (attempt $attempt): ${e.message}")
                lastException = e
                // Continue to next attempt for IOExceptions (like unexpected end of stream)
            } catch (e: Exception) {
                Log.e("PARKIRIN_REPO", "Non-IO Error: ${e.message}", e)
                return Result.failure(e)
            }
        }

        return Result.failure(lastException ?: Exception("Unknown error during recordEntry"))
    }
}
