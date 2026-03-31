package com.netra.parkirin.transaction.payment.service

import com.netra.parkirin.transaction.invoice.model.InvoiceKey
import com.netra.parkirin.transaction.invoice.repository.InvoiceRepository
import com.netra.parkirin.transaction.lottery.model.LotteryEntry
import com.netra.parkirin.transaction.lottery.model.LotteryKey
import com.netra.parkirin.transaction.lottery.repository.LotteryEntryRepository
import com.netra.parkirin.transaction.parking.model.ParkingSessionKey
import com.netra.parkirin.transaction.parking.repository.ParkingSessionRepository
import com.netra.parkirin.transaction.payment.dto.CashPaymentRequest
import com.netra.parkirin.transaction.payment.dto.CashPaymentResponse
import com.netra.parkirin.transaction.payment.model.Payment
import com.netra.parkirin.transaction.payment.model.PaymentKey
import com.netra.parkirin.transaction.payment.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Service
class PaymentService(
    private val invoiceRepository: InvoiceRepository,
    private val paymentRepository: PaymentRepository,
    private val parkingSessionRepository: ParkingSessionRepository,
    private val lotteryEntryRepository: LotteryEntryRepository,
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    fun cashPayment(
        invoiceId: UUID,
        zoneId: UUID,
        invoiceDate: LocalDate,
        request: CashPaymentRequest,
        officerId: UUID,
    ): Mono<CashPaymentResponse> {
        val invoiceKey = InvoiceKey(zoneId = zoneId, invoiceDate = invoiceDate, id = invoiceId)

        return invoiceRepository.findById(invoiceKey)
            .switchIfEmpty(Mono.error(NoSuchElementException("Invoice not found: $invoiceId")))
            .flatMap { invoice ->
                if (invoice.status != "UNPAID") {
                    return@flatMap Mono.error(IllegalStateException("Invoice is not UNPAID, current status: ${invoice.status}"))
                }
                if (request.amountReceived < invoice.amount) {
                    return@flatMap Mono.error(IllegalArgumentException("Amount received is less than invoice amount"))
                }

                val now = Instant.now()
                val paymentDate = LocalDate.now()
                val receiptNumber = generateReceiptNumber()
                val paymentKey = PaymentKey(zoneId = zoneId, paymentDate = paymentDate, id = UUID.randomUUID())

                val payment = Payment(
                    key = paymentKey,
                    invoiceId = invoiceId,
                    method = "CASH",
                    amount = invoice.amount,
                    receiptNumber = receiptNumber,
                    paidAt = now,
                    createdAt = now,
                )

                paymentRepository.save(payment).flatMap { savedPayment ->
                    // Update invoice status to PAID
                    val updatedInvoice = invoice.copy(status = "PAID", updatedAt = now)
                    invoiceRepository.save(updatedInvoice).flatMap { _ ->
                        // Find the parking session and update to PAID
                        parkingSessionRepository.findBySessionId(invoice.sessionId).flatMap { session ->
                            val updatedSession = session.copy(status = "PAID", updatedAt = now)
                            parkingSessionRepository.save(updatedSession)
                        }.onErrorResume { ex ->
                            log.warn("Could not update parking session for invoice $invoiceId: ${ex.message}")
                            Mono.empty()
                        }.then(
                            // Insert lottery entry
                            run {
                                val lotteryCode = generateLotteryCode()
                                val lotteryKey = LotteryKey(
                                    period = currentLotteryPeriod(),
                                    id = UUID.randomUUID(),
                                )
                                val lotteryEntry = LotteryEntry(
                                    key = lotteryKey,
                                    paymentId = savedPayment.key.id,
                                    plateNumber = invoice.plateNumber,
                                    zoneId = zoneId,
                                    code = lotteryCode,
                                    createdAt = now,
                                )
                                lotteryEntryRepository.save(lotteryEntry).map { savedLottery ->
                                    CashPaymentResponse(
                                        paymentId = savedPayment.key.id,
                                        invoiceId = invoiceId,
                                        receiptNumber = receiptNumber,
                                        amount = invoice.amount,
                                        amountReceived = request.amountReceived,
                                        change = request.amountReceived - invoice.amount,
                                        lotteryCode = savedLottery.code,
                                        paidAt = now,
                                    )
                                }
                            }
                        )
                    }
                }
            }
    }

    fun qrisPaymentStub(invoiceId: UUID): Mono<Map<String, String>> {
        log.info("QRIS payment stub for invoice: $invoiceId")
        return Mono.just(
            mapOf(
                "invoiceId" to invoiceId.toString(),
                "status" to "PENDING",
                "message" to "QRIS payment gateway not yet integrated",
            )
        )
    }

    private fun generateReceiptNumber(): String = "RCP-${System.currentTimeMillis()}"
    private fun generateLotteryCode(): String = UUID.randomUUID().toString().take(8).uppercase()
    private fun currentLotteryPeriod(): String = YearMonth.now().toString()
}
