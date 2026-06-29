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

    @Column("sessionnumber") // ✅ Sesuai DB: huruf kecil semua
    val sessionNumber: String? = null,

    @Column("platenumber") // 🚀 FIX UTAMA: Ubah dari "plate_number" jadi "platenumber" gess!
    val plateNumber: String? = null,

    @Column("vehicleid") // ✅ Sesuai DB: huruf kecil semua
    val vehicleId: UUID? = null,

    @Column("vehicletype") // ✅ Sesuai DB: huruf kecil semua
    val vehicleType: String? = null,

    @Column("streetid") // ✅ Sesuai DB: huruf kecil semua
    val streetId: UUID? = null,

    @Column("streetname") // ✅ Sesuai DB: huruf kecil semua
    val streetName: String? = null,

    @Column("entryofficerid") // ✅ Sesuai DB: huruf kecil semua
    val entryOfficerId: UUID? = null,

    @Column("exitofficerid") // ✅ Sesuai DB: huruf kecil semua
    val exitOfficerId: UUID? = null,

    @Column("entrytime") // 🚀 FIX: Ubah dari "entry_time" jadi "entrytime" biar dapet waktu aslinya
    val entryTime: Instant = Instant.now(),

    @Column("exittime") // ✅ Sesuai DB: huruf kecil semua
    val exitTime: Instant? = null,

    @Column("entrylat") // 🚀 FIX: Ubah jadi huruf kecil semua tanpa underscore gess!
    val entryLat: Double? = null,

    @Column("entrylng") // ✅ Sesuai DB
    val entryLng: Double? = null,

    @Column("entryaccuracy") // ✅ Sesuai DB
    val entryAccuracy: Float? = null,

    @Column("exitlat") // ✅ Sesuai DB
    val exitLat: Double? = null,

    @Column("exitlng") // ✅ Sesuai DB
    val exitLng: Double? = null,

    @Column("exitaccuracy") // ✅ Sesuai DB
    val exitAccuracy: Float? = null,

    @Column("entryphotourl") // 🚀 FIX: dari entry_photo_url jadi entryphotourl
    val entryPhotoUrl: String? = null,

    @Column("exitphotourl") // 🚀 FIX: dari exit_photo_url jadi exitphotourl
    val exitPhotoUrl: String? = null,

    @Column("issubscription") // 🚀 FIX: dari is_subscription jadi issubscription
    val isSubscription: Boolean = false,

    val status: String = "ACTIVE",

    @Column("createdat") // 🚀 FIX: dari created_at jadi createdat
    val createdAt: Instant = Instant.now(),

    @Column("updatedat") // 🚀 FIX: dari updated_at jadi updatedat
    val updatedAt: Instant = Instant.now(),
)