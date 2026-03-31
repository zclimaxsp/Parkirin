package com.netra.parkirin.transaction.masterservice.dto

import java.util.UUID

data class VehicleDto(
    val id: UUID,
    val plateNumber: String,
    val vehicleType: String?,
    val ownerName: String?,
    val ownerPhone: String?,
    val ownerUserId: UUID?,
)
