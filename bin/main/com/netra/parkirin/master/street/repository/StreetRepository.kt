package com.netra.parkirin.master.street.repository

import com.netra.parkirin.master.street.model.Street
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.UUID

interface StreetRepository : ReactiveCrudRepository<Street, UUID>, StreetQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<Street>
    fun findAllByIdTariffZoneAndDeletedAtIsNull(idTariffZone: UUID): Flux<Street>
}
