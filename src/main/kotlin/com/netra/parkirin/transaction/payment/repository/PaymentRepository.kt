package com.netra.parkirin.transaction.payment.repository

import com.netra.parkirin.transaction.payment.model.Payment
import com.netra.parkirin.transaction.payment.model.PaymentKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import java.util.UUID

interface PaymentRepository : ReactiveCassandraRepository<Payment, PaymentKey> {

    @Query("SELECT * FROM payments WHERE invoice_id = ?0 ALLOW FILTERING")
    fun findByInvoiceId(invoiceId: UUID): Flux<Payment>
}
