package com.netra.parkirin.transaction.payment.service

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.netra.parkirin.transaction.invoice.model.Invoice
import com.netra.parkirin.transaction.invoice.model.InvoiceKey
import com.netra.parkirin.transaction.parking.repository.ParkingSessionRepository
import com.netra.parkirin.transaction.payment.dto.CashPaymentRequest
import com.netra.parkirin.transaction.payment.dto.CashPaymentResponse
import com.netra.parkirin.transaction.payment.model.Payment
import com.netra.parkirin.transaction.payment.model.PaymentKey
import com.netra.parkirin.transaction.payment.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID


@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val parkingSessionRepository: ParkingSessionRepository,
    //private val lotteryEntryRepository: LotteryEntryRepository,
    private val cassandraOperations: ReactiveCassandraOperations,
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    // NOTE: nama kolom di Cassandra LOKAL kamu itu TANPA underscore
    // (createdat, durationminutes, expiredat, invoicenumber, platenumber,
    // qrtoken, sessionid, updatedat) — beda dari schema.cql yang sudah basi.
    // Raw query di bawah ini sengaja pakai nama kolom asli biar match.

    private fun mapRowToInvoice(row: com.datastax.oss.driver.api.core.cql.Row): Invoice {
        return Invoice(
            key = InvoiceKey(
                zoneId = row.getUuid("zone_id")!!,
                invoiceDate = row.getLocalDate("invoice_date")!!,
                id = row.getUuid("id")!!,
            ),
            invoiceNumber = row.getString("invoicenumber") ?: "",
            sessionId = row.getUuid("sessionid")!!,
            plateNumber = row.getString("platenumber") ?: "",
            amount = if (row.isNull("amount")) 0L else row.getLong("amount"),
            durationMinutes = if (row.isNull("durationminutes")) 0 else row.getInt("durationminutes"),
            status = row.getString("status") ?: "UNPAID",
            expiredAt = row.getInstant("expiredat"),
            qrToken = row.getString("qrtoken"),
            createdAt = row.getInstant("createdat") ?: Instant.now(),
            updatedAt = row.getInstant("updatedat") ?: Instant.now(),
        )
    }

    private fun findInvoiceRaw(zoneId: UUID, invoiceDate: LocalDate, invoiceId: UUID): Mono<Invoice> {
        val cql = "SELECT zone_id, invoice_date, id, invoicenumber, sessionid, platenumber, " +
                "amount, durationminutes, status, expiredat, qrtoken, createdat, updatedat " +
                "FROM invoices WHERE zone_id = ? AND invoice_date = ? AND id = ?"

        val statement = SimpleStatement.newInstance(cql, zoneId, invoiceDate, invoiceId)

        return cassandraOperations.reactiveCqlOperations.queryForRows(statement)
            .next()
            .map { row -> mapRowToInvoice(row) }
    }

    /**
     * Fallback kalau invoiceDate yang dikirim FE meleset dari tanggal asli invoice
     * (misal sesi parkir lewat tengah malam).
     */
    private fun findInvoiceByIdAnyDate(invoiceId: UUID): Mono<Invoice> {
        val cql = "SELECT zone_id, invoice_date, id, invoicenumber, sessionid, platenumber, " +
                "amount, durationminutes, status, expiredat, qrtoken, createdat, updatedat " +
                "FROM invoices WHERE id = ? ALLOW FILTERING"

        val statement = SimpleStatement.newInstance(cql, invoiceId)

        return cassandraOperations.reactiveCqlOperations.queryForRows(statement)
            .next()
            .map { row -> mapRowToInvoice(row) }
    }

    private fun saveInvoiceRaw(invoice: Invoice): Mono<Invoice> {
        val cql = "UPDATE invoices SET status = ?, updatedat = ? " +
                "WHERE zone_id = ? AND invoice_date = ? AND id = ?"

        val statement = SimpleStatement.newInstance(
            cql,
            invoice.status,
            invoice.updatedAt,
            invoice.key.zoneId,
            invoice.key.invoiceDate,
            invoice.key.id,
        )

        return cassandraOperations.reactiveCqlOperations.execute(statement)
            .map { invoice }
    }

    fun cashPayment(
        invoiceId: UUID,
        zoneId: UUID,
        invoiceDate: LocalDate,
        request: CashPaymentRequest,
        officerId: UUID,
    ): Mono<CashPaymentResponse> {
        return findInvoiceRaw(zoneId, invoiceDate, invoiceId)
            .switchIfEmpty(findInvoiceByIdAnyDate(invoiceId))
            .switchIfEmpty(Mono.error(NoSuchElementException("Invoice not found: $invoiceId")))
            .flatMap { invoice ->
                if (invoice.status != "UNPAID") {
                    return@flatMap Mono.error<CashPaymentResponse>(
                        IllegalStateException("Invoice is not UNPAID, current status: ${invoice.status}")
                    )
                }
                if (request.amountReceived < invoice.amount) {
                    return@flatMap Mono.error<CashPaymentResponse>(
                        IllegalArgumentException("Amount received is less than invoice amount")
                    )
                }

                val now = Instant.now()
                val paymentDate = LocalDate.now()
                val receiptNumber = generateReceiptNumber()
                val paymentKey = PaymentKey(zoneId = invoice.key.zoneId, paymentDate = paymentDate, id = UUID.randomUUID())

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
                    val updatedInvoice = invoice.copy(status = "PAID", updatedAt = now)
                    saveInvoiceRaw(updatedInvoice).flatMap { _ ->
                        parkingSessionRepository.findBySessionId(invoice.sessionId).flatMap { session ->
                            val updatedSession = session.copy(status = "PAID", updatedAt = now)
                            parkingSessionRepository.save(updatedSession)
                        }.onErrorResume { ex ->
                            log.warn("Could not update parking session for invoice $invoiceId: ${ex.message}")
                            Mono.empty()
                        }.then(
                                Mono.just(
                                    CashPaymentResponse(
                                        paymentId = savedPayment.key.id,
                                        invoiceId = invoiceId,
                                        receiptNumber = receiptNumber,
                                        amount = invoice.amount,
                                        amountReceived = request.amountReceived,
                                        change = request.amountReceived - invoice.amount,
                                        lotteryCode = "",
                                        paidAt = now,
                                    )
                                )
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
}