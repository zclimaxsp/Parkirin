package com.netra.parkirin.master.vehicle.repository

import com.netra.parkirin.master.vehicle.model.Vehicle
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface VehicleRepository : ReactiveCrudRepository<Vehicle, UUID>, VehicleQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<Vehicle>
    fun findByPlateNumberAndDeletedAtIsNull(plateNumber: String): Mono<Vehicle>
    fun findAllByOwnerUserIdAndDeletedAtIsNull(ownerUserId: UUID): Flux<Vehicle>
}
