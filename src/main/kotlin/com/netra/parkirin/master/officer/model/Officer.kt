package com.netra.parkirin.master.officer.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("officers")
data class Officer(
    @Id
    val id: UUID? = null,
    val userId: UUID? = null,
    val name: String? = null,
    val nip: String? = null,
    val phone: String? = null,
    val assignedStreetId: UUID? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
