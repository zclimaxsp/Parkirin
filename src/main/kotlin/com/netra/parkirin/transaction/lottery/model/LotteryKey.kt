package com.netra.parkirin.transaction.lottery.model

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.util.UUID

@PrimaryKeyClass
data class LotteryKey(
    @PrimaryKeyColumn(name = "period", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val period: String,

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val id: UUID = UUID.randomUUID(),
) : Serializable
