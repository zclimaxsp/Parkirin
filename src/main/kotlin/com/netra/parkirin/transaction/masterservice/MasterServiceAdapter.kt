package com.netra.parkirin.transaction.masterservice

import com.netra.parkirin.transaction.masterservice.dto.SubscriptionCheckResponse
import com.netra.parkirin.transaction.masterservice.dto.TariffRuleDto
import com.netra.parkirin.transaction.masterservice.dto.VehicleDto
import com.netra.parkirin.transaction.masterservice.dto.VehicleValidateResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class MasterServiceAdapter(private val masterClient: WebClient) {

    private val log = LoggerFactory.getLogger(MasterServiceAdapter::class.java)

    fun validateVehicle(plate: String): Mono<VehicleValidateResponse> {
        return masterClient.get()
            .uri("/api/v1/vehicles/validate?plate={plate}", plate)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }) { Mono.empty() }
            .bodyToMono<VehicleValidateResponse>()
            .onErrorResume { ex ->
                log.warn("Vehicle validation failed for plate=$plate: ${ex.message}")
                Mono.empty()
            }
    }

    fun registerVehicle(plate: String, vehicleType: String): Mono<VehicleDto> {
        val body = mapOf("plateNumber" to plate, "vehicleType" to vehicleType)
        return masterClient.post()
            .uri("/api/v1/vehicles")
            .bodyValue(body)
            .retrieve()
            .bodyToMono<VehicleDto>()
            .onErrorResume { ex ->
                log.warn("Vehicle registration failed for plate=$plate: ${ex.message}")
                Mono.empty()
            }
    }

    fun checkSubscription(vehicleId: UUID, zoneId: UUID): Mono<SubscriptionCheckResponse> {
        return masterClient.get()
            .uri("/api/v1/subscriptions/check?vehicleId={vehicleId}&zoneId={zoneId}", vehicleId, zoneId)
            .retrieve()
            .bodyToMono<SubscriptionCheckResponse>()
            .onErrorResume { ex ->
                log.warn("Subscription check failed vehicleId=$vehicleId zoneId=$zoneId: ${ex.message}")
                Mono.just(SubscriptionCheckResponse(hasActiveSubscription = false))
            }
    }

    fun getTariffRules(zoneId: UUID): Mono<List<TariffRuleDto>> {
        return masterClient.get()
            .uri("/api/v1/tariff-rules/zone/{zoneId}", zoneId)
            .retrieve()
            .bodyToMono<List<TariffRuleDto>>()
            .onErrorResume { ex ->
                log.warn("Tariff rules fetch failed zoneId=$zoneId: ${ex.message}")
                Mono.just(emptyList())
            }
    }

    fun getVehicleOwnerPhone(vehicleId: UUID): Mono<String?> {
        return masterClient.get()
            .uri("/api/v1/vehicles/{vehicleId}", vehicleId)
            .retrieve()
            .bodyToMono<VehicleDto>()
            .map { it.ownerPhone }
            .onErrorResume { Mono.empty() }
    }
}
