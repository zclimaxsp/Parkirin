package com.netra.parkirin.master.subscription.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.subscription.dto.SubscriptionCheckResponse
import com.netra.parkirin.master.subscription.dto.SubscriptionDto
import com.netra.parkirin.master.subscription.dto.SubscriptionPlanDto
import com.netra.parkirin.master.subscription.service.SubscriptionPlanService
import com.netra.parkirin.master.subscription.service.SubscriptionService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class SubscriptionController(
    private val subscriptionService: SubscriptionService,
    private val planService: SubscriptionPlanService,
) {

    // --- Subscription Plans ---

    @GetMapping("/subscription-plans")
    fun getAllPlans(): Flux<SubscriptionPlanDto> = planService.findAll()

    @GetMapping("/subscription-plans/active")
    fun getActivePlans(): Flux<SubscriptionPlanDto> = planService.findActive()

    @PostMapping("/subscription-plans/paged")
    fun getPagedPlans(@RequestBody request: PageableRequest<SubscriptionPlanDto>): Mono<PageableResponse<SubscriptionPlanDto>> =
        planService.findAllSearchFilterPaged(request)

    @GetMapping("/subscription-plans/{id}")
    fun getPlanById(@PathVariable id: UUID): Mono<SubscriptionPlanDto> = planService.findById(id)

    @PostMapping("/subscription-plans")
    fun createPlan(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: SubscriptionPlanDto
    ): Mono<SubscriptionPlanDto> = planService.create(dto)

    @PutMapping("/subscription-plans/{id}")
    fun updatePlan(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: SubscriptionPlanDto
    ): Mono<SubscriptionPlanDto> = planService.update(id, dto)

    @DeleteMapping("/subscription-plans/{id}")
    fun deletePlan(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<SubscriptionPlanDto> = planService.delete(id)

    // --- Subscriptions ---

    @GetMapping("/subscriptions")
    fun getAll(): Flux<SubscriptionDto> = subscriptionService.findAll()

    @PostMapping("/subscriptions/paged")
    fun getPaged(@RequestBody request: PageableRequest<SubscriptionDto>): Mono<PageableResponse<SubscriptionDto>> =
        subscriptionService.findAllSearchFilterPaged(request)

    @GetMapping("/subscriptions/{id}")
    fun getById(@PathVariable id: UUID): Mono<SubscriptionDto> = subscriptionService.findById(id)

    @GetMapping("/subscriptions/vehicle/{vehicleId}")
    fun getByVehicle(@PathVariable vehicleId: UUID): Flux<SubscriptionDto> =
        subscriptionService.findByVehicle(vehicleId)

    @GetMapping("/subscriptions/my")
    fun getMine(@RequestHeader("X-User-Id") userId: String): Flux<SubscriptionDto> =
        subscriptionService.findByUser(UUID.fromString(userId))

    @GetMapping("/subscriptions/check")
    fun checkSubscription(
        @RequestParam vehicleId: UUID,
        @RequestParam zoneId: UUID,
    ): Mono<SubscriptionCheckResponse> = subscriptionService.checkSubscription(vehicleId, zoneId)

    @PostMapping("/subscriptions")
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: SubscriptionDto
    ): Mono<SubscriptionDto> = subscriptionService.create(dto)

    @PutMapping("/subscriptions/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: SubscriptionDto
    ): Mono<SubscriptionDto> = subscriptionService.update(id, dto)

    @PatchMapping("/subscriptions/{id}/cancel")
    fun cancel(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<SubscriptionDto> = subscriptionService.cancel(id)

    @DeleteMapping("/subscriptions/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<SubscriptionDto> = subscriptionService.delete(id)
}
