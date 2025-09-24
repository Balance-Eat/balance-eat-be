package org.balanceeat.domain.config

import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.sql.DataSource
import kotlin.reflect.full.findAnnotation

@Component
class DatabaseCleanUp(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager,
) {

    fun all() {
        val dataSource: DataSource = jdbcTemplate.dataSource
            ?: throw IllegalStateException("No DataSource available for cleanup")

        val productName = dataSource.connection.use { it.metaData.databaseProductName }

        val tables = entityManager.metamodel.entities
            .map { it.javaType }
            .mapNotNull { it.kotlin.findAnnotation<Table>()?.name }

        when {
            productName.contains("PostgreSQL", ignoreCase = true) -> {
                tables.forEach { table ->
                    jdbcTemplate.execute("TRUNCATE TABLE $table RESTART IDENTITY CASCADE")
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
                tables.forEach { table ->
                    jdbcTemplate.execute("TRUNCATE TABLE $table")
                }
            }
        }
    }
}