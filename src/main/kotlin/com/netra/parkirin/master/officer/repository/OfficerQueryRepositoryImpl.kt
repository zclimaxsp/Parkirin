package com.netra.parkirin.master.officer.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.officer.model.Officer
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class OfficerQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<Officer>(
    databaseClient = databaseClient,
    entityClass = Officer::class.java,
    entityMapper = { row, _ ->
        Officer(
            id = row.get("id", UUID::class.java)!!,
            userId = row.get("user_id", UUID::class.java),
            name = row.get("name", String::class.java),
            nip = row.get("nip", String::class.java),
            phone = row.get("phone", String::class.java),
            assignedStreetId = row.get("assigned_street_id", UUID::class.java),
            isActive = row.get("is_active", Boolean::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), OfficerQueryRepository
