package com.netra.parkirin.master.subscription.mapper

import com.netra.parkirin.master.subscription.dto.SubscriptionPlanDto
import com.netra.parkirin.master.subscription.model.SubscriptionPlan
import java.time.OffsetDateTime

fun SubscriptionPlan.toDto(): SubscriptionPlanDto = SubscriptionPlanDto(
    id = id,
    name = name,
    vehicleType = vehicleType,
    price = price,
    durationDays = durationDays,
    description = description,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SubscriptionPlanDto.toModel(existing: SubscriptionPlan? = null): SubscriptionPlan = SubscriptionPlan(
    id = existing?.id,
    name = this.name ?: existing?.name,
    vehicleType = this.vehicleType ?: existing?.vehicleType,
    price = this.price ?: existing?.price,
    durationDays = this.durationDays ?: existing?.durationDays,
    description = this.description ?: existing?.description,
    isActive = this.isActive ?: existing?.isActive,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
