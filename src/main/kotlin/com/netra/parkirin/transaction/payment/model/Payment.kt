package com.netra.parkirin.transaction.payment.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.UUID

// Kolom asli di DB lokal (DESCRIBE TABLE parkirin_oltp.payments):
// zone_id, payment_date, id, amount, createdat, invoiceid, "method", paidat, receiptnumber
// TIDAK ADA kolom gateway_ref / gatewayref sama sekali, jadi field itu dihapus
// dari model biar ga ada mismatch pas insert.
@Table("payments")
data class Payment(
    @PrimaryKey
    val key: PaymentKey,

    @Column("invoiceid")
    val invoiceId: UUID,

    @Column("method")
    val method: String = "CASH",

    val amount: Long = 0L,

    @Column("receiptnumber")
    val receiptNumber: String,

    @Column("paidat")
    val paidAt: Instant = Instant.now(),

    @Column("createdat")
    val createdAt: Instant = Instant.now(),
)