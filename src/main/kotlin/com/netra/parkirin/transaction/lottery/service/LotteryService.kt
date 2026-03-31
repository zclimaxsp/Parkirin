package com.netra.parkirin.transaction.lottery.service

import com.netra.parkirin.transaction.lottery.dto.LotteryEntryDto
import com.netra.parkirin.transaction.lottery.mapper.LotteryMapper
import com.netra.parkirin.transaction.lottery.model.LotteryEntry
import com.netra.parkirin.transaction.lottery.repository.LotteryEntryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LotteryService(
    private val lotteryEntryRepository: LotteryEntryRepository,
) {
    fun findByPeriod(period: String): Flux<LotteryEntryDto> {
        return lotteryEntryRepository.findByKeyPeriod(period)
            .map { LotteryMapper.toDto(it) }
    }

    fun saveEntry(entry: LotteryEntry): Mono<LotteryEntry> {
        return lotteryEntryRepository.save(entry)
    }
}
