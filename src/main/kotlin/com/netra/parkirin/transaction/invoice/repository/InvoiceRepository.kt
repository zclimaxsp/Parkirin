package com.netra.parkirin.transaction.invoice.repository

import com.netra.parkirin.transaction.invoice.model.Invoice
import com.netra.parkirin.transaction.invoice.model.InvoiceKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface InvoiceRepository : ReactiveCassandraRepository<Invoice, InvoiceKey> {

    @Query("SELECT * FROM invoices WHERE qr_token = ?0 ALLOW FILTERING")
    fun findByQrToken(qrToken: String): Mono<Invoice>

    @Query("SELECT * FROM invoices WHERE session_id = ?0 ALLOW FILTERING")
    fun findBySessionId(sessionId: UUID): Flux<Invoice>
}
