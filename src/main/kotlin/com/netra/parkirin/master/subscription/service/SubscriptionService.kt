package com.netra.parkirin.master.subscription.service

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.subscription.dto.SubscriptionCheckResponse
import com.netra.parkirin.master.subscription.dto.SubscriptionDto
import com.netra.parkirin.master.subscription.mapper.toDto
import com.netra.parkirin.master.subscription.mapper.toModel
import com.netra.parkirin.master.subscription.repository.SubscriptionPlanRepository
import com.netra.parkirin.master.subscription.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Service
class SubscriptionService(
    private val repository: SubscriptionRepository,
    private val planRepository: SubscriptionPlanRepository,
) {

    fun findAll(): Flux<SubscriptionDto> =
        repository.findAllByDeletedAtIsNull().map { it.toDto() }

    fun findByVehicle(vehicleId: UUID): Flux<SubscriptionDto> =
        repository.findAllByIdVehicleAndDeletedAtIsNull(vehicleId).map { it.toDto() }

    fun findByUser(userId: UUID): Flux<SubscriptionDto> =
        repository.findAllByUserIdAndDeletedAtIsNull(userId).map { it.toDto() }

    fun findById(id: UUID): Mono<SubscriptionDto> =
        repository.findById(id).filter { it.deletedAt == null }.map { it.toDto() }

    fun create(dto: SubscriptionDto): Mono<SubscriptionDto> =
        repository.save(dto.toModel()).map { it.toDto() }

    fun update(id: UUID, dto: SubscriptionDto): Mono<SubscriptionDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap { existing ->
            repository.save(dto.toModel(existing)).map { it.toDto() }
        }

    fun cancel(id: UUID): Mono<SubscriptionDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(status = "CANCELLED", updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun delete(id: UUID): Mono<SubscriptionDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(deletedAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun checkSubscription(vehicleId: UUID, zoneId: UUID): Mono<SubscriptionCheckResponse> {
        return repository.findActiveSubscriptionByVehicleAndZone(vehicleId, zoneId, LocalDate.now())
            .flatMap { sub ->
                planRepository.findById(sub.idPlan!!).map { plan ->
                    SubscriptionCheckResponse(
                        isActive = true,
                        planName = plan.name,
                        expiresAt = sub.endDate,
                    )
                }
            }
            .switchIfEmpty(
                Mono.just(
                    SubscriptionCheckResponse(
                        isActive = false,
                        planName = null,
                        expiresAt = null,
                    )
                )
            )
    }

    fun findAllSearchFilterPaged(request: PageableRequest<SubscriptionDto>): Mono<PageableResponse<SubscriptionDto>> {
        val filters = request.filterColumns()
        val search = request.search?.let {
            request.searchColumnNames().associateWith { request.search }
        }
        val sorts = request.sortColumns()

        val dataMono = repository.findAllPagedFiltered(
            page = request.page,
            size = request.size,
            search = search,
            filters = filters,
            sorts = sorts
        ).collectList()

        val countMono = repository.countAllFiltered(search, filters)

        return Mono.zip(dataMono, countMono).map {
            PageableResponse(
                page = request.page,
                size = request.size,
                totalElements = it.t2,
                content = it.t1.map { sub -> sub.toDto() }
            )
        }
    }
}
