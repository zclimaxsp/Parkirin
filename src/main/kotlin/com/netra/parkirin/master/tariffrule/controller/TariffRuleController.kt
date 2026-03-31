package com.netra.parkirin.master.tariffrule.controller

import com.netra.common.library.dto.pagination.PageableRequest
import com.netra.common.library.dto.pagination.PageableResponse
import com.netra.parkirin.master.tariffrule.dto.TariffRuleDto
import com.netra.parkirin.master.tariffrule.service.TariffRuleService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/tariff-rules")
class TariffRuleController(private val service: TariffRuleService) {

    @GetMapping
    fun getAll(): Flux<TariffRuleDto> = service.findAll()

    @GetMapping("/zone/{zoneId}")
    fun getByZone(@PathVariable zoneId: UUID): Flux<TariffRuleDto> = service.findByZone(zoneId)

    @PostMapping("/paged")
    fun getPaged(@RequestBody request: PageableRequest<TariffRuleDto>): Mono<PageableResponse<TariffRuleDto>> =
        service.findAllSearchFilterPaged(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<TariffRuleDto> = service.findById(id)

    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: TariffRuleDto
    ): Mono<TariffRuleDto> = service.create(dto)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
        @RequestBody dto: TariffRuleDto
    ): Mono<TariffRuleDto> = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @RequestHeader("X-User-Id", required = false) userId: String?,
    ): Mono<TariffRuleDto> = service.delete(id)
}
