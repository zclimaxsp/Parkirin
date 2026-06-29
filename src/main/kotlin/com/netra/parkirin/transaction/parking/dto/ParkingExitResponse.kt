package com.netra.parkirin.transaction.parking.dto

import java.time.Instant
import java.util.UUID

data class ParkingExitResponse(
    val sessionId: String,
    val sessionNumber: String,
    val plateNumber: String,
    val invoiceId: String, // 🚀 Pastiin ini udah masuk meks!
    val invoiceNumber: String,
    val amount: Long,
    val durationMinutes: Int,
    val qrToken: String,
    val exitTime: String
)
