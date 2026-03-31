package com.netra.parkirin.master.vehicle.dto

import java.util.UUID

data class VehicleValidateResponse(
    val vehicleId: UUID,
    val vehicleType: String,
    val isSubscriptionActive: Boolean,
    val subscriptionZoneId: UUID?,
)
