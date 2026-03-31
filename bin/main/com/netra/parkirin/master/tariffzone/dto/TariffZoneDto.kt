package com.netra.parkirin.master.tariffzone.dto

import java.time.OffsetDateTime
import java.util.UUID

data class TariffZoneDto(
    val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val boundary: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
