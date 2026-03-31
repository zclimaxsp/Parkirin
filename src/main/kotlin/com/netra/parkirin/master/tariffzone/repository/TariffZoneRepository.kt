package com.netra.parkirin.master.tariffzone.repository

import com.netra.parkirin.master.tariffzone.model.TariffZone
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface TariffZoneRepository : ReactiveCrudRepository<TariffZone, UUID>, TariffZoneQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<TariffZone>

    @Query("""
        SELECT id, name, description, is_active, created_at, updated_at
        FROM tariff_zones
        WHERE ST_Contains(boundary, ST_SetSRID(ST_Point(:lng, :lat), 4326))
          AND is_active = true
          AND deleted_at IS NULL
        LIMIT 1
    """)
    fun detectZone(lat: Double, lng: Double): Mono<TariffZone>
}
