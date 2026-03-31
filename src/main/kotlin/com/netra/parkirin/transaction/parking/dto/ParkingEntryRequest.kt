package com.netra.parkirin.transaction.parking.dto

import java.util.UUID

data class ParkingEntryRequest(
    val plateNumber: String,
    val vehicleType: String = "MOTORCYCLE",
    val zoneId: UUID,
    val streetId: UUID? = null,
    val streetName: String? = null,
    val entryLat: Double? = null,
    val entryLng: Double? = null,
    val entryAccuracy: Float? = null,
    val entryPhotoUrl: String? = null,
)
