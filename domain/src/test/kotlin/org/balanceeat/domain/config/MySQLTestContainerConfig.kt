package org.balanceeat.domain.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container

@TestConfiguration
class MySQLTestContainerConfig {

    companion object {
        @Container
        @JvmStatic
        private val MYSQL_CONTAINER: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true)

        init {
            if (!MYSQL_CONTAINER.isRunning) {
                MYSQL_CONTAINER.start()
            }
        }

        fun getContainer(): MySQLContainer<*> {
            return MYSQL_CONTAINER
        }
    }
}