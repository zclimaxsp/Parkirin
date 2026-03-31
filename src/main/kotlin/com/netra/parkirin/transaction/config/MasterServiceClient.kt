package com.netra.parkirin.transaction.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class MasterServiceClient {

    @Value("\${master-service.base-url:http://localhost:8080}")
    private lateinit var baseUrl: String

    @Bean
    fun masterClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
}
