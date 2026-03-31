package com.netra.parkirin.transaction.invoice.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("invoices")
data class Invoice(
    @PrimaryKey
    val key: InvoiceKey,

    @Column("invoice_number")
    val invoiceNumber: String,

    @Column("session_id")
    val sessionId: UUID,

    @Column("plate_number")
    val plateNumber: String,

    val amount: Long = 0L,

    @Column("duration_minutes")
    val durationMinutes: Int = 0,

    val status: String = "UNPAID",

    @Column("expired_at")
    val expiredAt: Instant? = null,

    @Column("qr_token")
    val qrToken: String? = null,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
)
