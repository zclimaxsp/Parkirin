package com.netra.parkirin.transaction.payment.repository

import com.netra.parkirin.transaction.payment.model.Payment
import com.netra.parkirin.transaction.payment.model.PaymentKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.util.UUID

interface PaymentRepository : ReactiveCassandraRepository<Payment, PaymentKey> {

    // 🚀 FIX: Ubah 'invoice_id' menjadi 'invoiceid' sesuai skema DB asli Cassandra gess!
    @Query("SELECT * FROM payments WHERE invoiceid = ?0 ALLOW FILTERING")
    fun findByInvoiceId(invoiceId: UUID): Flux<Payment>

    @Query("""
    SELECT * FROM payments
    WHERE zone_id = ?0
    AND payment_date = ?1
""")
    fun findByZoneIdAndPaymentDate(
        zoneId: UUID,
        paymentDate: LocalDate
    ): Flux<Payment>
}