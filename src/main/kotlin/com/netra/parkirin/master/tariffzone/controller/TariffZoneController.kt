package com.netra.parkirin.master.tariffzone.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.tariffzone.dto.TariffZoneDto
import com.netra.parkirin.master.tariffzone.dto.ZoneDetectRequest
import com.netra.parkirin.master.tariffzone.dto.ZoneDetectResponse
import com.netra.parkirin.master.tariffzone.service.TariffZoneService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/zones")
class TariffZoneController(private val service: TariffZoneService) {

    @GetMapping
    fun getAll(): Flux<TariffZoneDto> = service.findAll()

    @PostMapping("/paged")
    fun getPaged(@RequestBody request: PageableRequest<TariffZoneDto>): Mono<PageableResponse<TariffZoneDto>> =
        service.findAllSearchFilterPaged(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<TariffZoneDto> = service.findById(id)

    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: TariffZoneDto
    ): Mono<TariffZoneDto> = service.create(dto)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: TariffZoneDto
    ): Mono<TariffZoneDto> = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<TariffZoneDto> = service.delete(id)

    @PostMapping("/detect")
    fun detectZone(@RequestBody request: ZoneDetectRequest): Mono<ZoneDetectResponse> =
        service.detectZone(request)
}
