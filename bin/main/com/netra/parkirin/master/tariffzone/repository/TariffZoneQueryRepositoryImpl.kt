package com.netra.parkirin.master.tariffzone.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.tariffzone.model.TariffZone
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class TariffZoneQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<TariffZone>(
    databaseClient = databaseClient,
    entityClass = TariffZone::class.java,
    entityMapper = { row, _ ->
        TariffZone(
            id = row.get("id", UUID::class.java)!!,
            name = row.get("name", String::class.java),
            description = row.get("description", String::class.java),
            boundary = null,
            isActive = row.get("is_active", Boolean::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), TariffZoneQueryRepository
