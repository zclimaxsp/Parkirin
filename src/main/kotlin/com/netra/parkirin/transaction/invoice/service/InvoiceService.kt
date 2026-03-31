package com.netra.parkirin.transaction.invoice.service

import com.netra.parkirin.transaction.invoice.dto.InvoiceDto
import com.netra.parkirin.transaction.invoice.mapper.InvoiceMapper
import com.netra.parkirin.transaction.invoice.model.Invoice
import com.netra.parkirin.transaction.invoice.model.InvoiceKey
import com.netra.parkirin.transaction.invoice.repository.InvoiceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID

@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
) {
    private val log = LoggerFactory.getLogger(InvoiceService::class.java)

    fun findByQrToken(qrToken: String): Mono<InvoiceDto> {
        return invoiceRepository.findByQrToken(qrToken)
            .map { InvoiceMapper.toDto(it) }
    }

    fun findById(zoneId: UUID, invoiceDate: LocalDate, id: UUID): Mono<InvoiceDto> {
        val key = InvoiceKey(zoneId = zoneId, invoiceDate = invoiceDate, id = id)
        return invoiceRepository.findById(key)
            .map { InvoiceMapper.toDto(it) }
    }

    fun getInvoiceStatus(zoneId: UUID, invoiceDate: LocalDate, id: UUID): Mono<Map<String, String>> {
        val key = InvoiceKey(zoneId = zoneId, invoiceDate = invoiceDate, id = id)
        return invoiceRepository.findById(key)
            .map { mapOf("id" to it.key.id.toString(), "status" to it.status) }
            .switchIfEmpty(Mono.error(NoSuchElementException("Invoice not found: $id")))
    }

    fun notifyByInvoiceId(id: UUID): Mono<Map<String, String>> {
        log.info("WA notification stub for invoice: $id")
        return Mono.just(mapOf("status" to "notification_queued", "invoiceId" to id.toString()))
    }

    fun saveInvoice(invoice: Invoice): Mono<Invoice> {
        return invoiceRepository.save(invoice)
    }

    fun updateInvoiceStatus(invoice: Invoice, status: String): Mono<Invoice> {
        val updated = invoice.copy(status = status)
        return invoiceRepository.save(updated)
    }
}
