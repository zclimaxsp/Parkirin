package com.netra.parkirin.master.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.netra.parkirin.master"])
class R2dbcConfig
