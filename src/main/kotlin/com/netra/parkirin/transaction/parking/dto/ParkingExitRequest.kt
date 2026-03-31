package com.netra.parkirin.transaction.parking.dto

import java.util.UUID

data class ParkingExitRequest(
    val plateNumber: String,
    val zoneId: UUID? = null,
    val exitLat: Double? = null,
    val exitLng: Double? = null,
    val exitAccuracy: Float? = null,
    val exitPhotoUrl: String? = null,
)
