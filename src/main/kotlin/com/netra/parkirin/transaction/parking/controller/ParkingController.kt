package com.netra.parkirin.transaction.parking.controller

import com.netra.parkirin.transaction.common.UserContext
import com.netra.parkirin.transaction.parking.dto.ParkingEntryRequest
import com.netra.parkirin.transaction.parking.dto.ParkingEntryResponse
import com.netra.parkirin.transaction.parking.dto.ParkingExitRequest
import com.netra.parkirin.transaction.parking.dto.ParkingExitResponse
import com.netra.parkirin.transaction.parking.dto.ParkingSessionDto
import com.netra.parkirin.transaction.parking.service.ParkingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/parking")
class ParkingController(private val parkingService: ParkingService) {

    @PostMapping("/entry")
    fun entry(
        @RequestBody request: ParkingEntryRequest,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ParkingEntryResponse>> {
        val officerId = UserContext.requireUserId(exchange)
        return parkingService.parkingEntry(request, officerId)
            .map { ResponseEntity.ok(it) }
    }

    @PostMapping("/exit")
    fun exit(
        @RequestBody request: ParkingExitRequest,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ParkingExitResponse>> {
        val officerId = UserContext.requireUserId(exchange)
        return parkingService.parkingExit(request, officerId)
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/sessions")
    fun listSessions(
        @RequestParam zoneId: UUID,
        @RequestParam(required = false) date: LocalDate?,
    ): Flux<ParkingSessionDto> {
        val sessionDate = date ?: LocalDate.now()
        return parkingService.listSessions(zoneId, sessionDate)
    }

    @GetMapping("/sessions/{id}")
    fun getSession(
        @PathVariable id: UUID,
        @RequestParam zoneId: UUID,
        @RequestParam(required = false) date: LocalDate?,
    ): Mono<ResponseEntity<ParkingSessionDto>> {
        val sessionDate = date ?: LocalDate.now()
        return parkingService.getSessionById(zoneId, sessionDate, id)
            .map { ResponseEntity.ok(it) }
    }
}
