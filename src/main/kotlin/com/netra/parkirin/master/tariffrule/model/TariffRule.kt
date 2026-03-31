package com.netra.parkirin.master.tariffrule.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("tariff_rules")
data class TariffRule(
    @Id
    val id: UUID? = null,
    val idTariffZone: UUID? = null,
    val vehicleType: String? = null,
    val firstDurationHours: Int? = 2,
    val firstRate: Long? = null,
    val perHourRate: Long? = null,
    val maxDailyRate: Long? = null,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
