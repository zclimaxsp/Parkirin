package com.netra.parkirin.master.tariffrule.dto

import java.time.OffsetDateTime
import java.util.UUID

data class TariffRuleDto(
    val id: UUID? = null,
    val idTariffZone: UUID? = null,
    val vehicleType: String? = null,
    val firstDurationHours: Int? = 2,
    val firstRate: Long? = null,
    val perHourRate: Long? = null,
    val maxDailyRate: Long? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
