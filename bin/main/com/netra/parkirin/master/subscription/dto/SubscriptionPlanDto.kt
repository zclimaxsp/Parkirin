package com.netra.parkirin.master.subscription.dto

import java.time.OffsetDateTime
import java.util.UUID

data class SubscriptionPlanDto(
    val id: UUID? = null,
    val name: String? = null,
    val vehicleType: String? = null,
    val price: Long? = null,
    val durationDays: Int? = 30,
    val description: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
