package com.netra.parkirin.transaction.parking.service

import com.netra.parkirin.transaction.invoice.model.Invoice
import com.netra.parkirin.transaction.invoice.model.InvoiceKey
import com.netra.parkirin.transaction.invoice.repository.InvoiceRepository
import com.netra.parkirin.transaction.lottery.model.LotteryEntry
import com.netra.parkirin.transaction.lottery.model.LotteryKey
import com.netra.parkirin.transaction.lottery.repository.LotteryEntryRepository
import com.netra.parkirin.transaction.masterservice.MasterServiceAdapter
import com.netra.parkirin.transaction.masterservice.dto.TariffRuleDto
import com.netra.parkirin.transaction.parking.dto.ParkingEntryRequest
import com.netra.parkirin.transaction.parking.dto.ParkingEntryResponse
import com.netra.parkirin.transaction.parking.dto.ParkingExitRequest
import com.netra.parkirin.transaction.parking.dto.ParkingExitResponse
import com.netra.parkirin.transaction.parking.dto.ParkingSessionDto
import com.netra.parkirin.transaction.parking.mapper.ParkingSessionMapper
import com.netra.parkirin.transaction.parking.model.ParkingSession
import com.netra.parkirin.transaction.parking.model.ParkingSessionKey
import com.netra.parkirin.transaction.parking.repository.ParkingSessionRepository
import com.netra.parkirin.transaction.payment.model.Payment
import com.netra.parkirin.transaction.payment.model.PaymentKey
import com.netra.parkirin.transaction.payment.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.min

@Service
class ParkingService(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val invoiceRepository: InvoiceRepository,
    private val paymentRepository: PaymentRepository,
    private val lotteryEntryRepository: LotteryEntryRepository,
    private val masterServiceAdapter: MasterServiceAdapter,
) {
    private val log = LoggerFactory.getLogger(ParkingService::class.java)

    fun parkingEntry(request: ParkingEntryRequest, officerId: UUID): Mono<ParkingEntryResponse> {
        val zoneId = request.zoneId

        // Step 1: Validate vehicle, auto-register if not found
        val vehicleMono = masterServiceAdapter.validateVehicle(request.plateNumber)
            .switchIfEmpty {
                log.info("Vehicle not found, auto-registering plate=${request.plateNumber}")
                masterServiceAdapter.registerVehicle(request.plateNumber, request.vehicleType)
                    .map { vehicle ->
                        com.netra.parkirin.transaction.masterservice.dto.VehicleValidateResponse(
                            id = vehicle.id,
                            plateNumber = vehicle.plateNumber,
                            vehicleType = vehicle.vehicleType,
                            ownerName = vehicle.ownerName,
                            ownerPhone = vehicle.ownerPhone,
                            ownerUserId = vehicle.ownerUserId,
                        )
                    }
            }

        return vehicleMono.flatMap { vehicle ->
            // Step 2: Check subscription
            masterServiceAdapter.checkSubscription(vehicle.id, zoneId).flatMap { subscriptionCheck ->
                val isSubscription = subscriptionCheck.hasActiveSubscription
                val sessionNumber = generateSessionNumber()
                val now = Instant.now()
                val sessionDate = LocalDate.now()
                val sessionId = UUID.randomUUID()

                val sessionKey = ParkingSessionKey(
                    zoneId = zoneId,
                    sessionDate = sessionDate,
                    id = sessionId,
                )

                val status = if (isSubscription) "PAID" else "ACTIVE"

                val session = ParkingSession(
                    key = sessionKey,
                    sessionNumber = sessionNumber,
                    plateNumber = request.plateNumber,
                    vehicleId = vehicle.id,
                    vehicleType = vehicle.vehicleType ?: request.vehicleType,
                    streetId = request.streetId,
                    streetName = request.streetName,
                    entryOfficerId = officerId,
                    entryTime = now,
                    entryLat = request.entryLat,
                    entryLng = request.entryLng,
                    entryAccuracy = request.entryAccuracy,
                    entryPhotoUrl = request.entryPhotoUrl,
                    isSubscription = isSubscription,
                    status = status,
                )

                parkingSessionRepository.save(session).flatMap { savedSession ->
                    if (isSubscription) {
                        // Insert invoice (amount=0, PAID), payment (SUBSCRIPTION), lottery
                        val invoiceId = UUID.randomUUID()
                        val invoiceKey = InvoiceKey(zoneId = zoneId, invoiceDate = sessionDate, id = invoiceId)
                        val invoice = Invoice(
                            key = invoiceKey,
                            invoiceNumber = generateInvoiceNumber(),
                            sessionId = sessionId,
                            plateNumber = request.plateNumber,
                            amount = 0L,
                            durationMinutes = 0,
                            status = "PAID",
                            qrToken = UUID.randomUUID().toString(),
                            createdAt = now,
                            updatedAt = now,
                        )
                        val paymentKey = PaymentKey(zoneId = zoneId, paymentDate = sessionDate, id = UUID.randomUUID())
                        val payment = Payment(
                            key = paymentKey,
                            invoiceId = invoiceId,
                            method = "SUBSCRIPTION",
                            amount = 0L,
                            receiptNumber = generateReceiptNumber(),
                            paidAt = now,
                            createdAt = now,
                        )
                        val lotteryCode = generateLotteryCode()
                        val lotteryKey = LotteryKey(period = currentLotteryPeriod(), id = UUID.randomUUID())
                        val lotteryEntry = LotteryEntry(
                            key = lotteryKey,
                            paymentId = paymentKey.id,
                            plateNumber = request.plateNumber,
                            zoneId = zoneId,
                            code = lotteryCode,
                        )

                        invoiceRepository.save(invoice)
                            .flatMap { paymentRepository.save(payment) }
                            .flatMap { lotteryEntryRepository.save(lotteryEntry) }
                            .map { lottery ->
                                ParkingEntryResponse(
                                    sessionId = sessionId,
                                    sessionNumber = sessionNumber,
                                    plateNumber = request.plateNumber,
                                    vehicleType = savedSession.vehicleType,
                                    zoneId = zoneId,
                                    isSubscription = true,
                                    status = status,
                                    entryTime = now,
                                    invoiceId = invoiceId,
                                    lotteryCode = lotteryCode,
                                )
                            }
                    } else {
                        Mono.just(
                            ParkingEntryResponse(
                                sessionId = sessionId,
                                sessionNumber = sessionNumber,
                                plateNumber = request.plateNumber,
                                vehicleType = savedSession.vehicleType,
                                zoneId = zoneId,
                                isSubscription = false,
                                status = status,
                                entryTime = now,
                            )
                        )
                    }
                }
            }
        }
    }

    fun parkingExit(request: ParkingExitRequest, officerId: UUID): Mono<ParkingExitResponse> {
        // Find active session by plate number
        return parkingSessionRepository.findActiveByPlateNumber(request.plateNumber)
            .next()
            .switchIfEmpty(Mono.error(NoSuchElementException("No active parking session for plate: ${request.plateNumber}")))
            .flatMap { session ->
                val now = Instant.now()
                val durationMinutes = ChronoUnit.MINUTES.between(session.entryTime, now).toInt().coerceAtLeast(1)
                val zoneId = session.key.zoneId

                // Fetch tariff rules
                masterServiceAdapter.getTariffRules(zoneId).flatMap { tariffRules ->
                    val vehicleType = session.vehicleType ?: "MOTORCYCLE"
                    val tariff = tariffRules.find { it.vehicleType == vehicleType }
                        ?: tariffRules.firstOrNull()
                        ?: TariffRuleDto(
                            id = UUID.randomUUID(),
                            idTariffZone = zoneId,
                            vehicleType = vehicleType,
                            firstDurationHours = 2,
                            firstRate = 2000L,
                            perHourRate = 1000L,
                        )

                    val amount = calculateAmount(tariff, durationMinutes)

                    // Update session
                    val updatedSession = session.copy(
                        status = "PENDING_PAYMENT",
                        exitOfficerId = officerId,
                        exitTime = now,
                        exitLat = request.exitLat,
                        exitLng = request.exitLng,
                        exitAccuracy = request.exitAccuracy,
                        exitPhotoUrl = request.exitPhotoUrl,
                        updatedAt = now,
                    )

                    parkingSessionRepository.save(updatedSession).flatMap { _ ->
                        val invoiceDate = LocalDate.now()
                        val invoiceId = UUID.randomUUID()
                        val qrToken = UUID.randomUUID().toString()
                        val invoiceNumber = generateInvoiceNumber()

                        val invoiceKey = InvoiceKey(zoneId = zoneId, invoiceDate = invoiceDate, id = invoiceId)
                        val invoice = Invoice(
                            key = invoiceKey,
                            invoiceNumber = invoiceNumber,
                            sessionId = session.key.id,
                            plateNumber = session.plateNumber,
                            amount = amount,
                            durationMinutes = durationMinutes,
                            status = "UNPAID",
                            expiredAt = now.plus(24, ChronoUnit.HOURS),
                            qrToken = qrToken,
                            createdAt = now,
                            updatedAt = now,
                        )

                        invoiceRepository.save(invoice).map { savedInvoice ->
                            ParkingExitResponse(
                                sessionId = session.key.id,
                                sessionNumber = session.sessionNumber,
                                plateNumber = session.plateNumber,
                                invoiceId = invoiceId,
                                invoiceNumber = invoiceNumber,
                                amount = amount,
                                durationMinutes = durationMinutes,
                                qrToken = qrToken,
                                exitTime = now,
                            )
                        }
                    }
                }
            }
    }

    fun listSessions(zoneId: UUID, date: LocalDate): Flux<ParkingSessionDto> {
        return parkingSessionRepository.findByKeyZoneIdAndKeySessionDate(zoneId, date)
            .map { ParkingSessionMapper.toDto(it) }
    }

    fun getSessionById(zoneId: UUID, sessionDate: LocalDate, id: UUID): Mono<ParkingSessionDto> {
        val key = ParkingSessionKey(zoneId = zoneId, sessionDate = sessionDate, id = id)
        return parkingSessionRepository.findById(key)
            .map { ParkingSessionMapper.toDto(it) }
            .switchIfEmpty(Mono.error(NoSuchElementException("Session not found: $id")))
    }

    private fun calculateAmount(tariff: TariffRuleDto, durationMinutes: Int): Long {
        val firstDurationMinutes = tariff.firstDurationHours * 60
        val amount: Long = if (durationMinutes <= firstDurationMinutes) {
            tariff.firstRate
        } else {
            val extraHours = ceil((durationMinutes - firstDurationMinutes) / 60.0).toLong()
            tariff.firstRate + extraHours * tariff.perHourRate
        }
        return if (tariff.maxDailyRate != null) min(amount, tariff.maxDailyRate) else amount
    }

    private fun generateSessionNumber(): String =
        "PKR-${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}-${(1000..9999).random()}"

    private fun generateInvoiceNumber(): String =
        "INV-${System.currentTimeMillis()}"

    private fun generateReceiptNumber(): String =
        "RCP-${System.currentTimeMillis()}"

    private fun generateLotteryCode(): String =
        UUID.randomUUID().toString().take(8).uppercase()

    private fun currentLotteryPeriod(): String =
        YearMonth.now().toString()
}
