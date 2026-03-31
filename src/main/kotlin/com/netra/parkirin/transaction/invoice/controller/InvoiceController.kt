package com.netra.parkirin.transaction.invoice.controller

import com.netra.parkirin.transaction.invoice.dto.InvoiceDto
import com.netra.parkirin.transaction.invoice.service.InvoiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/invoices")
class InvoiceController(private val invoiceService: InvoiceService) {

    @GetMapping("/{id}")
    fun getInvoice(
        @PathVariable id: UUID,
        @RequestParam zoneId: UUID,
        @RequestParam(required = false) date: LocalDate?,
    ): Mono<ResponseEntity<InvoiceDto>> {
        val invoiceDate = date ?: LocalDate.now()
        return invoiceService.findById(zoneId, invoiceDate, id)
            .map { ResponseEntity.ok(it) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping("/{id}/status")
    fun getInvoiceStatus(
        @PathVariable id: UUID,
        @RequestParam zoneId: UUID,
        @RequestParam(required = false) date: LocalDate?,
    ): Mono<ResponseEntity<Map<String, String>>> {
        val invoiceDate = date ?: LocalDate.now()
        return invoiceService.getInvoiceStatus(zoneId, invoiceDate, id)
            .map { ResponseEntity.ok(it) }
            .onErrorReturn(ResponseEntity.notFound().build())
    }

    @PostMapping("/{id}/notify")
    fun notifyInvoice(@PathVariable id: UUID): Mono<ResponseEntity<Map<String, String>>> {
        return invoiceService.notifyByInvoiceId(id)
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/qr/{qrToken}")
    fun getInvoiceByQr(@PathVariable qrToken: String): Mono<ResponseEntity<InvoiceDto>> {
        return invoiceService.findByQrToken(qrToken)
            .map { ResponseEntity.ok(it) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }
}
