package com.netra.parkirin.master.officer.mapper

import com.netra.parkirin.master.officer.dto.OfficerDto
import com.netra.parkirin.master.officer.model.Officer
import java.time.OffsetDateTime

fun Officer.toDto(): OfficerDto = OfficerDto(
    id = id,
    userId = userId,
    name = name,
    nip = nip,
    phone = phone,
    assignedStreetId = assignedStreetId,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun OfficerDto.toModel(existing: Officer? = null): Officer = Officer(
    id = existing?.id,
    userId = this.userId ?: existing?.userId,
    name = this.name ?: existing?.name,
    nip = this.nip ?: existing?.nip,
    phone = this.phone ?: existing?.phone,
    assignedStreetId = this.assignedStreetId ?: existing?.assignedStreetId,
    isActive = this.isActive ?: existing?.isActive,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
