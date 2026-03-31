package com.netra.parkirin.master.vehicle.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.vehicle.dto.VehicleDto
import com.netra.parkirin.master.vehicle.dto.VehicleValidateResponse
import com.netra.parkirin.master.vehicle.service.VehicleService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/vehicles")
class VehicleController(private val service: VehicleService) {

    @GetMapping
    fun getAll(): Flux<VehicleDto> = service.findAll()

    @PostMapping("/paged")
    fun getPaged(@RequestBody request: PageableRequest<VehicleDto>): Mono<PageableResponse<VehicleDto>> =
        service.findAllSearchFilterPaged(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<VehicleDto> = service.findById(id)

    @GetMapping("/plate/{plateNumber}")
    fun getByPlate(@PathVariable plateNumber: String): Mono<VehicleDto> = service.findByPlate(plateNumber)

    @GetMapping("/my")
    fun getMyVehicles(@RequestHeader("X-User-Id") userId: String): Flux<VehicleDto> =
        service.findByOwner(UUID.fromString(userId))

    @GetMapping("/validate")
    fun validate(@RequestParam plate: String): Mono<VehicleValidateResponse> = service.validate(plate)

    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: VehicleDto
    ): Mono<VehicleDto> = service.create(dto)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: VehicleDto
    ): Mono<VehicleDto> = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<VehicleDto> = service.delete(id)
}
