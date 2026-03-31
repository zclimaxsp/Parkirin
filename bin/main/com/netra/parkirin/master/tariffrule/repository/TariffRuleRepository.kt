package com.netra.parkirin.master.tariffrule.repository

import com.netra.parkirin.master.tariffrule.model.TariffRule
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.UUID

interface TariffRuleRepository : ReactiveCrudRepository<TariffRule, UUID>, TariffRuleQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<TariffRule>
    fun findAllByIdTariffZoneAndDeletedAtIsNull(idTariffZone: UUID): Flux<TariffRule>
}
