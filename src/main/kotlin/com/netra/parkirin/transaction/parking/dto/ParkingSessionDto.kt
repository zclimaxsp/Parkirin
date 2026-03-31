package com.netra.parkirin.transaction.parking.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class ParkingSessionDto(
    val id: UUID,
    val zoneId: UUID,
    val sessionDate: LocalDate,
    val sessionNumber: String,
    val plateNumber: String,
    val vehicleId: UUID?,
    val vehicleType: String?,
    val streetId: UUID?,
    val streetName: String?,
    val entryOfficerId: UUID?,
    val exitOfficerId: UUID?,
    val entryTime: Instant,
    val exitTime: Instant?,
    val entryLat: Double?,
    val entryLng: Double?,
    val entryAccuracy: Float?,
    val exitLat: Double?,
    val exitLng: Double?,
    val exitAccuracy: Float?,
    val entryPhotoUrl: String?,
    val exitPhotoUrl: String?,
    val isSubscription: Boolean,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
