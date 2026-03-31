package com.netra.parkirin.transaction.lottery.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("lottery_entries")
data class LotteryEntry(
    @PrimaryKey
    val key: LotteryKey,

    @Column("payment_id")
    val paymentId: UUID? = null,

    @Column("plate_number")
    val plateNumber: String,

    @Column("zone_id")
    val zoneId: UUID,

    val code: String,

    @Column("is_winner")
    val isWinner: Boolean = false,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),
)
