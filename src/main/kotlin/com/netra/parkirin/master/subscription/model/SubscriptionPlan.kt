package com.netra.parkirin.master.subscription.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("subscription_plans")
data class SubscriptionPlan(
    @Id
    val id: UUID? = null,
    val name: String? = null,
    val vehicleType: String? = null,
    val price: Long? = null,
    val durationDays: Int? = 30,
    val description: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
