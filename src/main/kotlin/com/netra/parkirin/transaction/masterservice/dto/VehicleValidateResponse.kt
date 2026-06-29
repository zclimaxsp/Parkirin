package com.netra.parkirin.transaction.masterservice.dto

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonProperty

data class VehicleValidateResponse(
    @JsonProperty("vehicleId")
    val id: UUID,
    val vehicleType: String?,
    @JsonProperty("isSubscriptionActive")
    val isSubscriptionActive: Boolean = false,
    val subscriptionZoneId: UUID? = null,
    val plateNumber: String? = null,
    val ownerName: String? = null,
    val ownerPhone: String? = null,
    val ownerUserId: UUID? = null,
)