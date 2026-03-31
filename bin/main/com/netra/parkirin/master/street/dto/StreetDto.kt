package com.netra.parkirin.master.street.dto

import java.time.OffsetDateTime
import java.util.UUID

data class StreetDto(
    val id: UUID? = null,
    val name: String? = null,
    val segment: String? = null,
    val city: String? = null,
    val idTariffZone: UUID? = null,
    val path: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
