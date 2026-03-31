package com.netra.parkirin.master.vehicle.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("vehicles")
data class Vehicle(
    @Id
    val id: UUID? = null,
    val plateNumber: String? = null,
    val vehicleType: String? = null,
    val ownerName: String? = null,
    val ownerPhone: String? = null,
    val ownerUserId: UUID? = null,
    val createdAt: OffsetDateTime? = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = OffsetDateTime.now(),
    val deletedAt: OffsetDateTime? = null,
)
