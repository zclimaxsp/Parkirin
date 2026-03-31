package com.netra.parkirin.transaction.parking.model

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

@PrimaryKeyClass
data class ParkingSessionKey(
    @PrimaryKeyColumn(name = "zone_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val zoneId: UUID,

    @PrimaryKeyColumn(name = "session_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    val sessionDate: LocalDate,

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val id: UUID = UUID.randomUUID(),
) : Serializable
