package com.netra.parkirin.master.subscription.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Table("subscriptions")
data class Subscription(
    @Id
    val id: UUID? = null,
    val idVehicle: UUID? = null,
    val idPlan: UUID? = null,
    val idTariffZone: UUID? = null,
    val userId: UUID? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val status: String? = "ACTIVE",
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
