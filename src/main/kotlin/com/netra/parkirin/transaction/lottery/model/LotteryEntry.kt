package com.netra.parkirin.transaction.lottery.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

// Kolom asli di DB lokal (DESCRIBE TABLE parkirin_oltp.lottery_entries):
// period, id, code, paymentid, platenumber, zone_id
// TIDAK ADA created_at / is_winner sama sekali, jadi 2 field itu dihapus
// dari model biar ga ada mismatch pas insert.
@Table("lottery_entries")
data class LotteryEntry(
    @PrimaryKey
    val key: LotteryKey,

    @Column("paymentid")
    val paymentId: UUID? = null,

    @Column("platenumber")
    val plateNumber: String,

    @Column("zone_id")
    val zoneId: UUID,

    val code: String,
)