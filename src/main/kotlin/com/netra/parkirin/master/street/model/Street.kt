package com.netra.parkirin.master.street.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("streets")
data class Street(
    @Id
    val id: UUID? = null,
    val name: String? = null,
    val segment: String? = null,
    val city: String? = null,
    val idTariffZone: UUID? = null,
    val path: String? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
