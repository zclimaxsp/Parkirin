package com.netra.parkirin.master.subscription.dto

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class SubscriptionDto(
    val id: UUID? = null,
    val idVehicle: UUID? = null,
    val idPlan: UUID? = null,
    val idTariffZone: UUID? = null,
    val userId: UUID? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val status: String? = "ACTIVE",
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
