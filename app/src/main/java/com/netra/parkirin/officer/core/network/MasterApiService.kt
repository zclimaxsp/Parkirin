package com.netra.parkirin.officer.core.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

data class OfficerResponse(
    val id: String,
    val userId: String,
    val name: String,
    val nip: String,
    val phone: String?,
    val assignedStreetId: String?,
    val isActive: Boolean
)

data class StreetResponse(
    val id: String,
    val name: String,
    val segment: String?,
    val city: String?,
    val idTariffZone: String?,
    val isActive: Boolean
)

data class ZoneResponse(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean
)

interface MasterApiService {

    @GET("api/v1/officers/me")
    suspend fun getMyProfile(
        @Header("X-User-Id") userId: String
    ): OfficerResponse

    @GET("api/v1/streets/{id}")
    suspend fun getStreetById(
        @Path("id") streetId: String
    ): StreetResponse

    @GET("api/v1/zones/{id}")
    suspend fun getZoneById(
        @Path("id") zoneId: String
    ): ZoneResponse

    @GET("api/v1/officers")
    suspend fun getAllOfficers(): List<OfficerResponse>
}