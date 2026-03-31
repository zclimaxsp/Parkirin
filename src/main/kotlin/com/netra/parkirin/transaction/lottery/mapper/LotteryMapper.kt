package com.netra.parkirin.transaction.lottery.mapper

import com.netra.parkirin.transaction.lottery.dto.LotteryEntryDto
import com.netra.parkirin.transaction.lottery.model.LotteryEntry

object LotteryMapper {
    fun toDto(entry: LotteryEntry): LotteryEntryDto {
        return LotteryEntryDto(
            id = entry.key.id,
            period = entry.key.period,
            paymentId = entry.paymentId,
            plateNumber = entry.plateNumber,
            zoneId = entry.zoneId,
            code = entry.code,
            isWinner = entry.isWinner,
            createdAt = entry.createdAt,
        )
    }
}
