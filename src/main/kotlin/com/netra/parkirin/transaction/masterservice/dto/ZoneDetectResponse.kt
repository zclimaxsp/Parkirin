package com.netra.parkirin.transaction.masterservice.dto

import java.util.UUID

data class ZoneDetectResponse(
    val zoneId: UUID,
    val zoneName: String,
    val locationId: UUID,
)
