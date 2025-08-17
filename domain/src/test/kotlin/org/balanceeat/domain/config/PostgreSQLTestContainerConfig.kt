package org.balanceeat.domain.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@TestConfiguration
class PostgreSQLTestContainerConfig {

    companion object {
        @Container
        @JvmStatic
        private val POSTGRESQL_CONTAINER: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true)

        init {
            if (!POSTGRESQL_CONTAINER.isRunning) {
                POSTGRESQL_CONTAINER.start()
            }
        }

        fun getContainer(): PostgreSQLContainer<*> {
            return POSTGRESQL_CONTAINER
        }
    }
}
