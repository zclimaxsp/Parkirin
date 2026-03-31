package com.netra.parkirin.transaction.parking.dto

import java.time.Instant
import java.util.UUID

data class ParkingExitResponse(
    val sessionId: UUID,
    val sessionNumber: String,
    val plateNumber: String,
    val invoiceId: UUID,
    val invoiceNumber: String,
    val amount: Long,
    val durationMinutes: Int,
    val qrToken: String,
    val exitTime: Instant,
)
