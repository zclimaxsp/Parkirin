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

    @Query("SELECT * FROM parking_sessions WHERE plate_number = ?0 AND status = 'ACTIVE' ALLOW FILTERING")
    fun findActiveByPlateNumber(plateNumber: String): Flux<ParkingSession>

    @Query("SELECT * FROM parking_sessions WHERE session_number = ?0 ALLOW FILTERING")
    fun findBySessionNumber(sessionNumber: String): Mono<ParkingSession>

    @Query("SELECT * FROM parking_sessions WHERE entry_officer_id = ?0 ALLOW FILTERING")
    fun findByEntryOfficerId(officerId: UUID): Flux<ParkingSession>

    @Query("SELECT * FROM parking_sessions WHERE id = ?0 ALLOW FILTERING")
    fun findBySessionId(sessionId: UUID): Mono<ParkingSession>
}
