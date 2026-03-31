package com.netra.parkirin.master.subscription.repository

import com.netra.parkirin.master.subscription.model.Subscription
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

interface SubscriptionRepository : ReactiveCrudRepository<Subscription, UUID>, SubscriptionQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<Subscription>
    fun findAllByIdVehicleAndDeletedAtIsNull(idVehicle: UUID): Flux<Subscription>
    fun findAllByUserIdAndDeletedAtIsNull(userId: UUID): Flux<Subscription>

    @Query("""
        SELECT * FROM subscriptions
        WHERE id_vehicle = :vehicleId
          AND status = 'ACTIVE'
          AND start_date <= :date
          AND end_date >= :date
          AND deleted_at IS NULL
        LIMIT 1
    """)
    fun findActiveSubscriptionByVehicleIdAndDate(vehicleId: UUID, date: LocalDate): Mono<Subscription>

    @Query("""
        SELECT * FROM subscriptions
        WHERE id_vehicle = :vehicleId
          AND id_tariff_zone = :zoneId
          AND status = 'ACTIVE'
          AND start_date <= :date
          AND end_date >= :date
          AND deleted_at IS NULL
        LIMIT 1
    """)
    fun findActiveSubscriptionByVehicleAndZone(vehicleId: UUID, zoneId: UUID, date: LocalDate): Mono<Subscription>
}
