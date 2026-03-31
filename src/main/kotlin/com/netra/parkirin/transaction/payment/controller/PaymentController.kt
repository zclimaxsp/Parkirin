package com.netra.parkirin.transaction.payment.controller

import com.netra.parkirin.transaction.common.UserContext
import com.netra.parkirin.transaction.payment.dto.CashPaymentRequest
import com.netra.parkirin.transaction.payment.dto.CashPaymentResponse
import com.netra.parkirin.transaction.payment.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(private val paymentService: PaymentService) {

    @PostMapping("/{invoiceId}/cash")
    fun cashPayment(
        @PathVariable invoiceId: UUID,
        @RequestParam zoneId: UUID,
        @RequestParam(required = false) date: LocalDate?,
        @RequestBody request: CashPaymentRequest,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<CashPaymentResponse>> {
        val officerId = UserContext.requireUserId(exchange)
        val invoiceDate = date ?: LocalDate.now()
        return paymentService.cashPayment(invoiceId, zoneId, invoiceDate, request, officerId)
            .map { ResponseEntity.ok(it) }
    }

    @PostMapping("/{invoiceId}/qris")
    fun qrisPayment(
        @PathVariable invoiceId: UUID,
    ): Mono<ResponseEntity<Map<String, String>>> {
        return paymentService.qrisPaymentStub(invoiceId)
            .map { ResponseEntity.ok(it) }
    }
}
