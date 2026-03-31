package com.netra.parkirin.master.officer.service

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.officer.dto.OfficerDto
import com.netra.parkirin.master.officer.mapper.toDto
import com.netra.parkirin.master.officer.mapper.toModel
import com.netra.parkirin.master.officer.repository.OfficerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.UUID

@Service
class OfficerService(private val repository: OfficerRepository) {

    fun findAll(): Flux<OfficerDto> =
        repository.findAllByDeletedAtIsNull().map { it.toDto() }

    fun findById(id: UUID): Mono<OfficerDto> =
        repository.findById(id).filter { it.deletedAt == null }.map { it.toDto() }

    fun findByUserId(userId: UUID): Mono<OfficerDto> =
        repository.findByUserIdAndDeletedAtIsNull(userId).map { it.toDto() }

    fun create(dto: OfficerDto): Mono<OfficerDto> =
        repository.save(dto.toModel()).map { it.toDto() }

    fun update(id: UUID, dto: OfficerDto): Mono<OfficerDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap { existing ->
            repository.save(dto.toModel(existing)).map { it.toDto() }
        }

    fun delete(id: UUID): Mono<OfficerDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(deletedAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun findAllSearchFilterPaged(request: PageableRequest<OfficerDto>): Mono<PageableResponse<OfficerDto>> {
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
                content = it.t1.map { officer -> officer.toDto() }
            )
        }
    }
}
