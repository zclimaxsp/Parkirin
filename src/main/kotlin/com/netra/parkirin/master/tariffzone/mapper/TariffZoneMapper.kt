package com.netra.parkirin.master.tariffzone.mapper

import com.netra.parkirin.master.tariffzone.dto.TariffZoneDto
import com.netra.parkirin.master.tariffzone.model.TariffZone
import java.time.OffsetDateTime

fun TariffZone.toDto(): TariffZoneDto = TariffZoneDto(
    id = id,
    name = name,
    description = description,
    boundary = boundary,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TariffZoneDto.toModel(existing: TariffZone? = null): TariffZone = TariffZone(
    id = existing?.id,
    name = this.name ?: existing?.name,
    description = this.description ?: existing?.description,
    boundary = this.boundary ?: existing?.boundary,
    isActive = this.isActive ?: existing?.isActive,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
