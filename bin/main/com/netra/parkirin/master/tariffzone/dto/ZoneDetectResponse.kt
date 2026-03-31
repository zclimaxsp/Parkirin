package com.netra.parkirin.master.tariffzone.dto

import java.util.UUID

data class ZoneDetectResponse(
    val zoneId: UUID,
    val zoneName: String,
    val isActive: Boolean,
)
