package com.netra.parkirin.transaction.parking.mapper

import com.netra.parkirin.transaction.parking.dto.ParkingSessionDto
import com.netra.parkirin.transaction.parking.model.ParkingSession

object ParkingSessionMapper {
    fun toDto(session: ParkingSession): ParkingSessionDto {
        return ParkingSessionDto(
            id = session.key.id,
            zoneId = session.key.zoneId,
            sessionDate = session.key.sessionDate,
            sessionNumber = session.sessionNumber,
            plateNumber = session.plateNumber,
            vehicleId = session.vehicleId,
            vehicleType = session.vehicleType,
            streetId = session.streetId,
            streetName = session.streetName,
            entryOfficerId = session.entryOfficerId,
            exitOfficerId = session.exitOfficerId,
            entryTime = session.entryTime,
            exitTime = session.exitTime,
            entryLat = session.entryLat,
            entryLng = session.entryLng,
            entryAccuracy = session.entryAccuracy,
            exitLat = session.exitLat,
            exitLng = session.exitLng,
            exitAccuracy = session.exitAccuracy,
            entryPhotoUrl = session.entryPhotoUrl,
            exitPhotoUrl = session.exitPhotoUrl,
            isSubscription = session.isSubscription,
            status = session.status,
            createdAt = session.createdAt,
            updatedAt = session.updatedAt,
        )
    }
}
