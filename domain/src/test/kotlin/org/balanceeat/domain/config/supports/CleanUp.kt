package org.balanceeat.domain.config.supports

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class CleanUp(
    private val jdbcTemplate: JdbcTemplate
) {

    fun all() {
        clearRDB()
    }

    private fun clearRDB() {
        val dataSource: DataSource = jdbcTemplate.dataSource
            ?: throw IllegalStateException("No DataSource available for cleanup")

        val productName = dataSource.connection.use { it.metaData.databaseProductName }

        // Define table names (add more as entities are added)
        val tables = listOf("user")

        when {
            productName.contains("PostgreSQL", ignoreCase = true) -> {
                tables.forEach { table ->
                    // Quote reserved identifiers for PostgreSQL
                    val quoted = "\"$table\""
                    jdbcTemplate.execute("TRUNCATE TABLE $quoted RESTART IDENTITY CASCADE")
                }
            }
            productName.contains("MySQL", ignoreCase = true) -> {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")
                tables.forEach { table ->
                    jdbcTemplate.execute("TRUNCATE TABLE $table")
                }
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")
            }
            else -> {
                // Fallback: try ANSI-compliant truncate (may fail depending on DB)
                tables.forEach { table ->
                    jdbcTemplate.execute("TRUNCATE TABLE $table")
                }
            }
        }
    }
}