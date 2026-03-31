package com.netra.parkirin.master.vehicle.mapper

import com.netra.parkirin.master.vehicle.dto.VehicleDto
import com.netra.parkirin.master.vehicle.model.Vehicle
import java.time.OffsetDateTime

fun Vehicle.toDto(): VehicleDto = VehicleDto(
    id = id,
    plateNumber = plateNumber,
    vehicleType = vehicleType,
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    ownerUserId = ownerUserId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun VehicleDto.toModel(existing: Vehicle? = null): Vehicle = Vehicle(
    id = existing?.id,
    plateNumber = this.plateNumber ?: existing?.plateNumber,
    vehicleType = this.vehicleType ?: existing?.vehicleType,
    ownerName = this.ownerName ?: existing?.ownerName,
    ownerPhone = this.ownerPhone ?: existing?.ownerPhone,
    ownerUserId = this.ownerUserId ?: existing?.ownerUserId,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
