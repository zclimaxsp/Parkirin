package com.netra.parkirin.transaction.payment.dto

import java.time.Instant
import java.util.UUID

data class CashPaymentResponse(
    val paymentId: UUID,
    val invoiceId: UUID,
    val receiptNumber: String,
    val amount: Long,
    val amountReceived: Long,
    val change: Long,
    val lotteryCode: String,
    val paidAt: Instant,
)
