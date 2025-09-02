package org.balanceeat.domain.config

import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer

@Configuration
class PostgreSQLTestContainerConfig {
    companion object {
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:14")
            .apply {
                withDatabaseName("testdb")
                withUsername("testuser")
                withPassword("testpass")
                withReuse(true)
                start()
            }

        init {
            val postgresJdbcUrl = postgreSQLContainer.let { "jdbc:postgresql://${it.host}:${it.firstMappedPort}/${it.databaseName}" }
            System.setProperty("spring.datasource.url", postgresJdbcUrl)
            System.setProperty("spring.datasource.username", postgreSQLContainer.username)
            System.setProperty("spring.datasource.password", postgreSQLContainer.password)
        }
    }
}
