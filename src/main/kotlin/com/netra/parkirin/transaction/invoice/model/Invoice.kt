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

    @Column("invoicenumber")
    val invoiceNumber: String,

    @Column("sessionid")
    val sessionId: UUID,

    @Column("platenumber")
    val plateNumber: String,

    val amount: Long = 0L,

    @Column("durationminutes")
    val durationMinutes: Int = 0,

    val status: String = "UNPAID",

    @Column("expiredat")
    val expiredAt: Instant? = null,

    @Column("qrtoken")
    val qrToken: String? = null,

    @Column("createdat")
    val createdAt: Instant = Instant.now(),

    @Column("updatedat")
    val updatedAt: Instant = Instant.now(),
)