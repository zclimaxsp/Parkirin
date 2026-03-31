package com.netra.parkirin.master.subscription.service

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.subscription.dto.SubscriptionPlanDto
import com.netra.parkirin.master.subscription.mapper.toDto
import com.netra.parkirin.master.subscription.mapper.toModel
import com.netra.parkirin.master.subscription.repository.SubscriptionPlanRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.UUID

@Service
class SubscriptionPlanService(private val repository: SubscriptionPlanRepository) {

    fun findAll(): Flux<SubscriptionPlanDto> =
        repository.findAllByDeletedAtIsNull().map { it.toDto() }

    fun findActive(): Flux<SubscriptionPlanDto> =
        repository.findAllByIsActiveTrueAndDeletedAtIsNull().map { it.toDto() }

    fun findById(id: UUID): Mono<SubscriptionPlanDto> =
        repository.findById(id).filter { it.deletedAt == null }.map { it.toDto() }

    fun create(dto: SubscriptionPlanDto): Mono<SubscriptionPlanDto> =
        repository.save(dto.toModel()).map { it.toDto() }

    fun update(id: UUID, dto: SubscriptionPlanDto): Mono<SubscriptionPlanDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap { existing ->
            repository.save(dto.toModel(existing)).map { it.toDto() }
        }

    fun delete(id: UUID): Mono<SubscriptionPlanDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(deletedAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun findAllSearchFilterPaged(request: PageableRequest<SubscriptionPlanDto>): Mono<PageableResponse<SubscriptionPlanDto>> {
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
                content = it.t1.map { plan -> plan.toDto() }
            )
        }
    }
}
