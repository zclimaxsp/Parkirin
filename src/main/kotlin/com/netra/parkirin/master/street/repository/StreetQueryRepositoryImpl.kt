package com.netra.parkirin.master.street.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.street.model.Street
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class StreetQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<Street>(
    databaseClient = databaseClient,
    entityClass = Street::class.java,
    entityMapper = { row, _ ->
        Street(
            id = row.get("id", UUID::class.java)!!,
            name = row.get("name", String::class.java),
            segment = row.get("segment", String::class.java),
            city = row.get("city", String::class.java),
            idTariffZone = row.get("id_tariff_zone", UUID::class.java),
            path = null,
            isActive = row.get("is_active", Boolean::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), StreetQueryRepository
