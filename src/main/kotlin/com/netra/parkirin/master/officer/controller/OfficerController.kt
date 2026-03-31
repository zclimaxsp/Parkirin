package com.netra.parkirin.master.officer.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.officer.dto.OfficerDto
import com.netra.parkirin.master.officer.service.OfficerService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/officers")
class OfficerController(private val service: OfficerService) {

    @GetMapping
    fun getAll(): Flux<OfficerDto> = service.findAll()

    @PostMapping("/paged")
    fun getPaged(@RequestBody request: PageableRequest<OfficerDto>): Mono<PageableResponse<OfficerDto>> =
        service.findAllSearchFilterPaged(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<OfficerDto> = service.findById(id)

    @GetMapping("/me")
    fun getMe(@RequestHeader("X-User-Id") userId: String): Mono<OfficerDto> =
        service.findByUserId(UUID.fromString(userId))

    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: OfficerDto
    ): Mono<OfficerDto> = service.create(dto)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: OfficerDto
    ): Mono<OfficerDto> = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<OfficerDto> = service.delete(id)
}
