package com.netra.parkirin.officer.feature.exit.data

import com.netra.parkirin.officer.core.network.MasterApiService
import com.netra.parkirin.officer.core.network.OfficerResponse
import com.netra.parkirin.officer.core.network.StreetResponse
import com.netra.parkirin.officer.core.network.ParkingExitRequest
import com.netra.parkirin.officer.core.network.ParkingExitResponse
import com.netra.parkirin.officer.core.network.ParkingSessionResponse
import com.netra.parkirin.officer.core.network.TransactionApiService
import com.netra.parkirin.officer.core.network.CashPaymentRequest
import com.netra.parkirin.officer.core.network.CashPaymentResponse
import com.netra.parkirin.officer.core.network.NotifyResponse
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class ExitRepository @Inject constructor(
    private val masterApiService: MasterApiService,
    private val transactionApiService: TransactionApiService
) {
    suspend fun getMyProfile(userId: String): Result<OfficerResponse> {
        return try {
            val response = masterApiService.getMyProfile(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStreetById(streetId: String): Result<StreetResponse> {
        return try {
            val response = masterApiService.getStreetById(streetId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * FITUR OTOMATIS LIST SESI AKTIF
     * Mengambil daftar kendaraan yang sedang parkir dan menghitung tarifnya secara front-end.
     */
    suspend fun getActiveSessions(userId: String, zoneId: String): Result<List<ParkingSessionResponse>> {
        return try {
            val response = transactionApiService.getActiveSessions(userId, zoneId)
            val processedSessions = response.map { session ->
                val (duration, amount) = calculateDurationAndAmount(session.entrytime)
                
                session.copy(
                    durationMinutes = duration, 
                    amount = amount
                )
            }
            Result.success(processedSessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionByPlate(
        userId: String,
        plateNumber: String,
        zoneId: String
    ): Result<ParkingSessionResponse> {
        return try {
            // Kembali ke habitat asli: nembak endpoint sessions dengan query plateNumber & zoneId
            val response = transactionApiService.getSessionByPlate(userId, plateNumber, zoneId)

            if (response.isNotEmpty()) {
                val session = response.first()
                // Hitung durasi dan tarif Rp 2.000 / jam
                val (duration, amount) = calculateDurationAndAmount(session.entrytime)
                Result.success(session.copy(durationMinutes = duration, amount = amount))
            } else {
                Result.failure(Exception("No active session found for this plate number in your zone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordExit(
        userId: String,
        plateNumber: String,
        zoneId: String,
        paymentMethod: String = "CASH"
    ): Result<ParkingExitResponse> {
        return try {
            val request = ParkingExitRequest(
                plateNumber = plateNumber,
                zoneId = zoneId,
                paymentMethod = paymentMethod
            )
            val response = transactionApiService.recordExit(userId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 1. Pastiin di fungsi repository lu menerima zoneId
    suspend fun processCashPayment(
        userId: String,
        invoiceId: String,
        zoneId: String, // 🚀 Pastiin fungsi repository lu juga nerima zoneId dari ViewModel ya!
        amountReceived: Long
    ): Result<CashPaymentResponse> {
        return try {
            val request = CashPaymentRequest(amountReceived = amountReceived)

            // Sesuaikan parameter panggilannya dengan interface Retrofit lu yang baru:
            val response = transactionApiService.processCashPayment(
                userId = userId,
                invoiceId = invoiceId,
                zoneId = zoneId, // 🚀 Masukin variabel zoneId-nya di sini
                request = request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun notifyAbsent(
        userId: String,
        invoiceId: String
    ): Result<NotifyResponse> {
        return try {
            val response = transactionApiService.notifyAbsent(userId, invoiceId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * RUMUS PERHITUNGAN DURASI & TARIF Rp 2.000 / JAM (Front-End)
     * Bulatkan ke atas per jam berikutnya. Contoh: 61 menit = 2 jam = Rp 4.000.
     */
    private fun calculateDurationAndAmount(entryTime: String?): Pair<Int, Long> {
        if (entryTime.isNullOrBlank()) return 0 to 2000L
        return try {
            val entry = ZonedDateTime.parse(entryTime).toInstant()
            val now = Instant.now()
            val duration = Duration.between(entry, now)
            val minutes = duration.toMinutes().toInt().coerceAtLeast(0)
            
            // Tarif Rp 2.000 per jam, minimal 1 jam.
            val hours = ceil(minutes / 60.0).toLong().coerceAtLeast(1L)
            val amount = hours * 2000
            
            minutes to amount
        } catch (e: Exception) {
            0 to 2000L // Default jika parsing gagal
        }
    }
}
