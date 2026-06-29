package com.netra.parkirin.officer.feature.history.data

import com.netra.parkirin.officer.core.network.MasterApiService
import com.netra.parkirin.officer.core.network.OfficerResponse
import com.netra.parkirin.officer.core.network.StreetResponse
import com.netra.parkirin.officer.core.network.ParkingSessionResponse
import com.netra.parkirin.officer.core.network.TransactionApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
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

    suspend fun getTodayHistory(userId: String, zoneId: String, date: String): Result<List<ParkingSessionResponse>> {
        return try {
            val response = transactionApiService.getParkingSessions(userId, zoneId, date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
