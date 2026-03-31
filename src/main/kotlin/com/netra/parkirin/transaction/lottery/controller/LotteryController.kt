package com.netra.parkirin.transaction.lottery.controller

import com.netra.parkirin.transaction.lottery.dto.LotteryEntryDto
import com.netra.parkirin.transaction.lottery.service.LotteryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.YearMonth

@RestController
@RequestMapping("/api/v1/lottery")
class LotteryController(private val lotteryService: LotteryService) {

    @GetMapping("/entries")
    fun listEntries(
        @RequestParam(required = false) period: String?,
    ): Flux<LotteryEntryDto> {
        val lotteryPeriod = period ?: YearMonth.now().toString()
        return lotteryService.findByPeriod(lotteryPeriod)
    }
}
