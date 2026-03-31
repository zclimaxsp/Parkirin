package com.netra.parkirin.transaction.payment.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class PaymentDto(
    val id: UUID,
    val zoneId: UUID,
    val paymentDate: LocalDate,
    val invoiceId: UUID,
    val method: String,
    val amount: Long,
    val receiptNumber: String,
    val gatewayRef: String?,
    val paidAt: Instant,
    val createdAt: Instant,
)
