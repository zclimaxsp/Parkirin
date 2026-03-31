package com.netra.parkirin.master.vehicle.service

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.subscription.repository.SubscriptionRepository
import com.netra.parkirin.master.vehicle.dto.VehicleDto
import com.netra.parkirin.master.vehicle.dto.VehicleValidateResponse
import com.netra.parkirin.master.vehicle.mapper.toDto
import com.netra.parkirin.master.vehicle.mapper.toModel
import com.netra.parkirin.master.vehicle.repository.VehicleRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Service
class VehicleService(
    private val repository: VehicleRepository,
    private val subscriptionRepository: SubscriptionRepository,
) {

    fun findAll(): Flux<VehicleDto> =
        repository.findAllByDeletedAtIsNull().map { it.toDto() }

    fun findById(id: UUID): Mono<VehicleDto> =
        repository.findById(id).filter { it.deletedAt == null }.map { it.toDto() }

    fun findByPlate(plateNumber: String): Mono<VehicleDto> =
        repository.findByPlateNumberAndDeletedAtIsNull(plateNumber).map { it.toDto() }

    fun findByOwner(ownerUserId: UUID): Flux<VehicleDto> =
        repository.findAllByOwnerUserIdAndDeletedAtIsNull(ownerUserId).map { it.toDto() }

    fun create(dto: VehicleDto): Mono<VehicleDto> =
        repository.save(dto.toModel()).map { it.toDto() }

    fun update(id: UUID, dto: VehicleDto): Mono<VehicleDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap { existing ->
            repository.save(dto.toModel(existing)).map { it.toDto() }
        }

    fun delete(id: UUID): Mono<VehicleDto> =
        repository.findById(id).filter { it.deletedAt == null }.flatMap {
            repository.save(it.copy(deletedAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())).map { it.toDto() }
        }

    fun validate(plateNumber: String): Mono<VehicleValidateResponse> {
        return repository.findByPlateNumberAndDeletedAtIsNull(plateNumber).flatMap { vehicle ->
            val vehicleId = vehicle.id!!
            subscriptionRepository
                .findActiveSubscriptionByVehicleIdAndDate(vehicleId, LocalDate.now())
                .map { sub ->
                    VehicleValidateResponse(
                        vehicleId = vehicleId,
                        vehicleType = vehicle.vehicleType ?: "",
                        isSubscriptionActive = true,
                        subscriptionZoneId = sub.idTariffZone,
                    )
                }
                .switchIfEmpty(
                    Mono.just(
                        VehicleValidateResponse(
                            vehicleId = vehicleId,
                            vehicleType = vehicle.vehicleType ?: "",
                            isSubscriptionActive = false,
                            subscriptionZoneId = null,
                        )
                    )
                )
        }
    }

    fun findAllSearchFilterPaged(request: PageableRequest<VehicleDto>): Mono<PageableResponse<VehicleDto>> {
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
                content = it.t1.map { vehicle -> vehicle.toDto() }
            )
        }
    }
}
