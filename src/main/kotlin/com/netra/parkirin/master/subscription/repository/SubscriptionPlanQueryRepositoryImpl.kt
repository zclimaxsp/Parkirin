package com.netra.parkirin.master.subscription.repository

import com.netra.common.library.repository.BaseQueryRepository
import com.netra.parkirin.master.subscription.model.SubscriptionPlan
import org.springframework.r2dbc.core.DatabaseClient
import java.time.OffsetDateTime
import java.util.UUID

class SubscriptionPlanQueryRepositoryImpl(
    databaseClient: DatabaseClient
) : BaseQueryRepository<SubscriptionPlan>(
    databaseClient = databaseClient,
    entityClass = SubscriptionPlan::class.java,
    entityMapper = { row, _ ->
        SubscriptionPlan(
            id = row.get("id", UUID::class.java)!!,
            name = row.get("name", String::class.java),
            vehicleType = row.get("vehicle_type", String::class.java),
            price = row.get("price", Long::class.java),
            durationDays = row.get("duration_days", Integer::class.java)?.toInt(),
            description = row.get("description", String::class.java),
            isActive = row.get("is_active", Boolean::class.java),
            createdAt = row.get("created_at", OffsetDateTime::class.java),
            updatedAt = row.get("updated_at", OffsetDateTime::class.java),
        )
    }
), SubscriptionPlanQueryRepository
