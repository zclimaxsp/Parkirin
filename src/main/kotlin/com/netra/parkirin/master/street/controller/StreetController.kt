package com.netra.parkirin.master.street.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.street.dto.StreetDto
import com.netra.parkirin.master.street.service.StreetService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/streets")
class StreetController(private val service: StreetService) {

    @GetMapping
    fun getAll(): Flux<StreetDto> = service.findAll()

    @GetMapping("/zone/{zoneId}")
    fun getByZone(@PathVariable zoneId: UUID): Flux<StreetDto> = service.findByZone(zoneId)

    @PostMapping("/paged")
    fun getPaged(@RequestBody request: PageableRequest<StreetDto>): Mono<PageableResponse<StreetDto>> =
        service.findAllSearchFilterPaged(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<StreetDto> = service.findById(id)

    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: StreetDto
    ): Mono<StreetDto> = service.create(dto)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: StreetDto
    ): Mono<StreetDto> = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<StreetDto> = service.delete(id)
}
