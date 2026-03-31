package com.netra.parkirin.master.vehicle.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.vehicle.model.Vehicle
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class VehicleQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<Vehicle>(
    databaseClient = databaseClient,
    entityClass = Vehicle::class.java,
    entityMapper = { row, _ ->
        Vehicle(
            id = row.get("id", UUID::class.java)!!,
            plateNumber = row.get("plate_number", String::class.java),
            vehicleType = row.get("vehicle_type", String::class.java),
            ownerName = row.get("owner_name", String::class.java),
            ownerPhone = row.get("owner_phone", String::class.java),
            ownerUserId = row.get("owner_user_id", UUID::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), VehicleQueryRepository
