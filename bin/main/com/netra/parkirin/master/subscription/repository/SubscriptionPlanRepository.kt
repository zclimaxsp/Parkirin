package com.netra.parkirin.master.subscription.repository

import com.netra.parkirin.master.subscription.model.SubscriptionPlan
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.UUID

interface SubscriptionPlanRepository : ReactiveCrudRepository<SubscriptionPlan, UUID>, SubscriptionPlanQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<SubscriptionPlan>
    fun findAllByIsActiveTrueAndDeletedAtIsNull(): Flux<SubscriptionPlan>
}
