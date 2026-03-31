package com.netra.parkirin.master.tariffzone.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("tariff_zones")
data class TariffZone(
    @Id
    val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val boundary: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
