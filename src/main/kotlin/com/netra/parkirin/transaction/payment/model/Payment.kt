package com.netra.parkirin.transaction.payment.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("payments")
data class Payment(
    @PrimaryKey
    val key: PaymentKey,

    @Column("invoice_id")
    val invoiceId: UUID,

    val method: String = "CASH",

    val amount: Long = 0L,

    @Column("receipt_number")
    val receiptNumber: String,

    @Column("gateway_ref")
    val gatewayRef: String? = null,

    @Column("paid_at")
    val paidAt: Instant = Instant.now(),

    @Column("created_at")
    val createdAt: Instant = Instant.now(),
)
