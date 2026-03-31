package com.netra.parkirin.master.subscription.mapper

import com.netra.parkirin.master.subscription.dto.SubscriptionDto
import com.netra.parkirin.master.subscription.model.Subscription
import java.time.OffsetDateTime

fun Subscription.toDto(): SubscriptionDto = SubscriptionDto(
    id = id,
    idVehicle = idVehicle,
    idPlan = idPlan,
    idTariffZone = idTariffZone,
    userId = userId,
    startDate = startDate,
    endDate = endDate,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SubscriptionDto.toModel(existing: Subscription? = null): Subscription = Subscription(
    id = existing?.id,
    idVehicle = this.idVehicle ?: existing?.idVehicle,
    idPlan = this.idPlan ?: existing?.idPlan,
    idTariffZone = this.idTariffZone ?: existing?.idTariffZone,
    userId = this.userId ?: existing?.userId,
    startDate = this.startDate ?: existing?.startDate,
    endDate = this.endDate ?: existing?.endDate,
    status = this.status ?: existing?.status,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
