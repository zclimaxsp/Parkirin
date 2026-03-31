package com.netra.parkirin.master.street.mapper

import com.netra.parkirin.master.street.dto.StreetDto
import com.netra.parkirin.master.street.model.Street
import java.time.OffsetDateTime

fun Street.toDto(): StreetDto = StreetDto(
    id = id,
    name = name,
    segment = segment,
    city = city,
    idTariffZone = idTariffZone,
    path = path,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun StreetDto.toModel(existing: Street? = null): Street = Street(
    id = existing?.id,
    name = this.name ?: existing?.name,
    segment = this.segment ?: existing?.segment,
    city = this.city ?: existing?.city,
    idTariffZone = this.idTariffZone ?: existing?.idTariffZone,
    path = this.path ?: existing?.path,
    isActive = this.isActive ?: existing?.isActive,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
