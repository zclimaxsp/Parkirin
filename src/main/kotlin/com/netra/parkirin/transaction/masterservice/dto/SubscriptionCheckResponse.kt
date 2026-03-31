package com.netra.parkirin.transaction.masterservice.dto

import java.util.UUID

data class SubscriptionCheckResponse(
    val hasActiveSubscription: Boolean,
    val subscriptionId: UUID? = null,
    val planName: String? = null,
)
