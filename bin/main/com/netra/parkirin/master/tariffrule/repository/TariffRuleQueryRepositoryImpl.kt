package com.netra.parkirin.master.tariffrule.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.tariffrule.model.TariffRule
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class TariffRuleQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<TariffRule>(
    databaseClient = databaseClient,
    entityClass = TariffRule::class.java,
    entityMapper = { row, _ ->
        TariffRule(
            id = row.get("id", UUID::class.java)!!,
            idTariffZone = row.get("id_tariff_zone", UUID::class.java),
            vehicleType = row.get("vehicle_type", String::class.java),
            firstDurationHours = row.get("first_duration_hours", Integer::class.java)?.toInt(),
            firstRate = row.get("first_rate", Long::class.java),
            perHourRate = row.get("per_hour_rate", Long::class.java),
            maxDailyRate = row.get("max_daily_rate", Long::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), TariffRuleQueryRepository
