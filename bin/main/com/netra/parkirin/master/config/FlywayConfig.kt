package com.netra.parkirin.master.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfig {

    @Value("\${spring.datasource.url}")
    lateinit var dataSourceUrl: String

    @Value("\${spring.datasource.username}")
    lateinit var dataSourceUsername: String

    @Value("\${spring.datasource.password}")
    lateinit var dataSourcePassword: String

    @Value("\${spring.flyway.locations:classpath:db/migration}")
    lateinit var flywayLocations: String

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        return Flyway.configure()
            .dataSource(dataSourceUrl, dataSourceUsername, dataSourcePassword)
            .locations(flywayLocations)
            .baselineOnMigrate(true)
            .load()
    }
}
