package com.netra.parkirin.transaction.parking.repository

import com.netra.parkirin.transaction.parking.model.ParkingSession
import com.netra.parkirin.transaction.parking.model.ParkingSessionKey
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

interface ParkingSessionRepository : ReactiveCassandraRepository<ParkingSession, ParkingSessionKey> {

    @AllowFiltering
    fun findByKeyZoneIdAndKeySessionDate(zoneId: UUID, sessionDate: LocalDate): Flux<ParkingSession>

    // 🚀 FIX: Ubah nama fungsi biar match dengan Service (findActiveByPlateNumber)
    // Dan pastikan kolom DB ditulis 'platenumber'
    @Query("SELECT * FROM parking_sessions WHERE platenumber = ?0 AND status = 'ACTIVE' LIMIT 1 ALLOW FILTERING")
    fun findActiveByPlateNumber(plateNumber: String): Mono<ParkingSession>

    // 🚀 FIX: Ubah 'session_number' jadi 'sessionnumber' sesuai skema DB
    @Query("SELECT * FROM parking_sessions WHERE sessionnumber = ?0 ALLOW FILTERING")
    fun findBySessionNumber(sessionNumber: String): Mono<ParkingSession>

    // 🚀 FIX: Ubah 'entry_officer_id' jadi 'entryofficerid' sesuai skema DB
    @Query("SELECT * FROM parking_sessions WHERE entryofficerid = ?0 ALLOW FILTERING")
    fun findByEntryOfficerId(officerId: UUID): Flux<ParkingSession>

    // 🚀 FIX: Id adalah part dari Primary Key, sesuaikan pencariannya meks
    @Query("SELECT * FROM parking_sessions WHERE id = ?0 ALLOW FILTERING")
    fun findBySessionId(sessionId: UUID): Mono<ParkingSession>

}