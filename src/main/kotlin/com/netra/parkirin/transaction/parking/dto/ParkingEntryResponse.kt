package com.netra.parkirin.transaction.parking.dto

import java.time.Instant
import java.util.UUID

data class ParkingEntryResponse(
    val sessionId: UUID,
    val sessionNumber: String,
    val plateNumber: String,
    val vehicleType: String?,
    val zoneId: UUID,
    val isSubscription: Boolean,
    val status: String,
    val entryTime: Instant,
    val invoiceId: UUID? = null,
    val lotteryCode: String? = null,
)
