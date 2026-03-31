package com.netra.parkirin.transaction.lottery.dto

import java.time.Instant
import java.util.UUID

data class LotteryEntryDto(
    val id: UUID,
    val period: String,
    val paymentId: UUID?,
    val plateNumber: String,
    val zoneId: UUID,
    val code: String,
    val isWinner: Boolean,
    val createdAt: Instant,
)
