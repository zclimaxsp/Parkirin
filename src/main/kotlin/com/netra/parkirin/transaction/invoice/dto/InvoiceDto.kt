package com.netra.parkirin.transaction.invoice.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class InvoiceDto(
    val id: UUID,
    val zoneId: UUID,
    val invoiceDate: LocalDate,
    val invoiceNumber: String,
    val sessionId: UUID,
    val plateNumber: String,
    val amount: Long,
    val durationMinutes: Int,
    val status: String,
    val expiredAt: Instant?,
    val qrToken: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
