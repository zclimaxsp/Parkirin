package com.netra.parkirin.transaction.masterservice.dto

import java.util.UUID

data class TariffRuleDto(
    val id: UUID,
    val idTariffZone: UUID,
    val vehicleType: String?,
    val firstDurationHours: Int = 2,
    val firstRate: Long = 0L,
    val perHourRate: Long = 0L,
    val maxDailyRate: Long? = null,
)
