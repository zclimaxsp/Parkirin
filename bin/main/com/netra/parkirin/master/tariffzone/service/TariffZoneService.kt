package com.netra.parkirin.master.tariffzone.service

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.tariffzone.dto.TariffZoneDto
import com.netra.parkirin.master.tariffzone.dto.ZoneDetectRequest
import com.netra.parkirin.master.tariffzone.dto.ZoneDetectResponse
import com.netra.parkirin.master.tariffzone.mapper.toDto
import com.netra.parkirin.master.tariffzone.mapper.toModel
import com.netra.parkirin.master.tariffzone.repository.TariffZoneRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.UUID

@Service
class TariffZoneService(private val repository: TariffZoneRepository) {

    fun findAll(): Flux<TariffZoneDto> =
        repository.findAllByDeletedAtIsNull().map { it.toDto() }

    fun findById(id: UUID): Mono<TariffZoneDto> =
        repository.findById(id).filter { it.deletedAt == null }.map { it.toDto() }

    fun create(dto: TariffZoneDto): Mono<TariffZoneDto> =
        repository.save(dto.toModel()).map { it.toDto() }

    fun update(id: UUID, dto: TariffZoneDto): Mono<TariffZoneDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap { existing ->
            repository.save(dto.toModel(existing)).map { it.toDto() }
        }

    fun delete(id: UUID): Mono<TariffZoneDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(deletedAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun detectZone(request: ZoneDetectRequest): Mono<ZoneDetectResponse> =
        repository.detectZone(request.lat, request.lng).map { zone ->
            ZoneDetectResponse(
                zoneId = zone.id!!,
                zoneName = zone.name ?: "",
                isActive = zone.isActive ?: true,
            )
        }

    fun findAllSearchFilterPaged(request: PageableRequest<TariffZoneDto>): Mono<PageableResponse<TariffZoneDto>> {
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
                content = it.t1.map { zone -> zone.toDto() }
            )
        }
    }
}
