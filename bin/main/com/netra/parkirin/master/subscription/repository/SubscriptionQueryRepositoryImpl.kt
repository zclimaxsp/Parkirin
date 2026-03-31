package com.netra.parkirin.master.subscription.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.subscription.model.Subscription
import org.springframework.r2dbc.core.DatabaseClient
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class SubscriptionQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<Subscription>(
    databaseClient = databaseClient,
    entityClass = Subscription::class.java,
    entityMapper = { row, _ ->
        Subscription(
            id = row.get("id", UUID::class.java)!!,
            idVehicle = row.get("id_vehicle", UUID::class.java),
            idPlan = row.get("id_plan", UUID::class.java),
            idTariffZone = row.get("id_tariff_zone", UUID::class.java),
            userId = row.get("user_id", UUID::class.java),
            startDate = row.get("start_date", LocalDate::class.java),
            endDate = row.get("end_date", LocalDate::class.java),
            status = row.get("status", String::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), SubscriptionQueryRepository
