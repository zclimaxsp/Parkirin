package com.netra.parkirin.transaction.parking.dto

data class ParkingSummaryResponse(
    val totalSessions: Int = 0,
    val activeSessions: Int = 0,
    val totalRevenue: Long = 0L,
    val avgDurationMinutes: Int = 0
)