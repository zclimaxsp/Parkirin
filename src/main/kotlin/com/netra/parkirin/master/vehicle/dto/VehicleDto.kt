package com.netra.parkirin.master.vehicle.dto

import java.time.OffsetDateTime
import java.util.UUID

data class VehicleDto(
    val id: UUID? = null,
    val plateNumber: String? = null,
    val vehicleType: String? = null,
    val ownerName: String? = null,
    val ownerPhone: String? = null,
    val ownerUserId: UUID? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
