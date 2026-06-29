package com.netra.parkirin.officer.feature.dashboard.data

import com.netra.parkirin.officer.core.network.DashboardSummaryResponse
import com.netra.parkirin.officer.core.network.MasterApiService
import com.netra.parkirin.officer.core.network.OfficerResponse
import com.netra.parkirin.officer.core.network.StreetResponse
import com.netra.parkirin.officer.core.network.TransactionApiService
import com.netra.parkirin.officer.core.network.ZoneResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
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

    suspend fun getZoneById(zoneId: String): Result<ZoneResponse> {
        return try {
            val response = masterApiService.getZoneById(zoneId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodaySummary(userId: String, zoneId: String): Result<DashboardSummaryResponse> {
        return try {
            val response = transactionApiService.getTodaySummary(userId, zoneId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
