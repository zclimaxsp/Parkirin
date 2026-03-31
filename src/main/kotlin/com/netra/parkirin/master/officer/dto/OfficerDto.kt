package com.netra.parkirin.master.officer.dto

import java.time.OffsetDateTime
import java.util.UUID

data class OfficerDto(
    val id: UUID? = null,
    val userId: UUID? = null,
    val name: String? = null,
    val nip: String? = null,
    val phone: String? = null,
    val assignedStreetId: UUID? = null,
    val isActive: Boolean? = true,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
