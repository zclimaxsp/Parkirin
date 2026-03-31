package com.netra.parkirin.transaction.parking.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("parking_sessions")
data class ParkingSession(
    @PrimaryKey
    val key: ParkingSessionKey,

    @Column("session_number")
    val sessionNumber: String,

    @Column("plate_number")
    val plateNumber: String,

    @Column("vehicle_id")
    val vehicleId: UUID? = null,

    @Column("vehicle_type")
    val vehicleType: String? = null,

    @Column("street_id")
    val streetId: UUID? = null,

    @Column("street_name")
    val streetName: String? = null,

    @Column("entry_officer_id")
    val entryOfficerId: UUID? = null,

    @Column("exit_officer_id")
    val exitOfficerId: UUID? = null,

    @Column("entry_time")
    val entryTime: Instant = Instant.now(),

    @Column("exit_time")
    val exitTime: Instant? = null,

    @Column("entry_lat")
    val entryLat: Double? = null,

    @Column("entry_lng")
    val entryLng: Double? = null,

    @Column("entry_accuracy")
    val entryAccuracy: Float? = null,

    @Column("exit_lat")
    val exitLat: Double? = null,

    @Column("exit_lng")
    val exitLng: Double? = null,

    @Column("exit_accuracy")
    val exitAccuracy: Float? = null,

    @Column("entry_photo_url")
    val entryPhotoUrl: String? = null,

    @Column("exit_photo_url")
    val exitPhotoUrl: String? = null,

    @Column("is_subscription")
    val isSubscription: Boolean = false,

    val status: String = "ACTIVE",

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
)
