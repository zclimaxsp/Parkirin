package com.netra.parkirin.master.tariffrule.mapper

import com.netra.parkirin.master.tariffrule.dto.TariffRuleDto
import com.netra.parkirin.master.tariffrule.model.TariffRule
import java.time.OffsetDateTime

fun TariffRule.toDto(): TariffRuleDto = TariffRuleDto(
    id = id,
    idTariffZone = idTariffZone,
    vehicleType = vehicleType,
    firstDurationHours = firstDurationHours,
    firstRate = firstRate,
    perHourRate = perHourRate,
    maxDailyRate = maxDailyRate,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TariffRuleDto.toModel(existing: TariffRule? = null): TariffRule = TariffRule(
    id = existing?.id,
    idTariffZone = this.idTariffZone ?: existing?.idTariffZone,
    vehicleType = this.vehicleType ?: existing?.vehicleType,
    firstDurationHours = this.firstDurationHours ?: existing?.firstDurationHours,
    firstRate = this.firstRate ?: existing?.firstRate,
    perHourRate = this.perHourRate ?: existing?.perHourRate,
    maxDailyRate = this.maxDailyRate ?: existing?.maxDailyRate,
    createdAt = existing?.createdAt,
    updatedAt = OffsetDateTime.now(),
    deletedAt = existing?.deletedAt,
)
