package com.netra.parkirin.master.subscription.dto

import java.time.LocalDate

data class SubscriptionCheckResponse(
    val isActive: Boolean,
    val planName: String?,
    val expiresAt: LocalDate?,
)
