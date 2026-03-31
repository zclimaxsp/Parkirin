package com.netra.parkirin.transaction.invoice.model

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

@PrimaryKeyClass
data class InvoiceKey(
    @PrimaryKeyColumn(name = "zone_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val zoneId: UUID,

    @PrimaryKeyColumn(name = "invoice_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    val invoiceDate: LocalDate,

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val id: UUID = UUID.randomUUID(),
) : Serializable
