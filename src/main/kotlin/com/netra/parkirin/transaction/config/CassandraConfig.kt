package com.netra.parkirin.transaction.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@EnableReactiveCassandraRepositories(basePackages = ["com.netra.parkirin.transaction"])
class CassandraConfig : AbstractReactiveCassandraConfiguration() {
    override fun getKeyspaceName(): String = "parkirin_oltp"
    override fun getEntityBasePackages(): Array<String> = arrayOf("com.netra.parkirin.transaction")
}
