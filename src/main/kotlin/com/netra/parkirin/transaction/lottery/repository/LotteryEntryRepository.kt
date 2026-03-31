package com.netra.parkirin.transaction.lottery.repository

import com.netra.parkirin.transaction.lottery.model.LotteryEntry
import com.netra.parkirin.transaction.lottery.model.LotteryKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface LotteryEntryRepository : ReactiveCassandraRepository<LotteryEntry, LotteryKey> {

    fun findByKeyPeriod(period: String): Flux<LotteryEntry>

    @Query("SELECT * FROM lottery_entries WHERE code = ?0 ALLOW FILTERING")
    fun findByCode(code: String): Mono<LotteryEntry>
}
