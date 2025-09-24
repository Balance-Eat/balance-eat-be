package org.balanceeat.domain.config

import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.sql.DataSource
import kotlin.reflect.full.findAnnotation

@Component
class DatabaseCleanUp(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager,
) {
    private val logger = LoggerFactory.getLogger(DatabaseCleanUp::class.java)

    fun all() {
        logger.info("🧹 Starting database cleanup...")

        val dataSource: DataSource = jdbcTemplate.dataSource
            ?: throw IllegalStateException("No DataSource available for cleanup")

        val productName = dataSource.connection.use { it.metaData.databaseProductName }
        logger.info("🗄️ Database type detected: {}", productName)

        // Automatically discover all entity tables
        val tables = entityManager.metamodel.entities
            .map { it.javaType }
            .mapNotNull { it.kotlin.findAnnotation<Table>()?.name }
        logger.info("📋 Tables to cleanup: {}", tables)

        when {
            productName.contains("PostgreSQL", ignoreCase = true) -> {
                logger.info("🐘 Using PostgreSQL cleanup strategy")
                tables.forEach { table ->
                    // Quote reserved identifiers for PostgreSQL
                    logger.debug("🗑️ Truncating PostgreSQL table: {}", table)
                    jdbcTemplate.execute("TRUNCATE TABLE $table RESTART IDENTITY CASCADE")
                }
            }
            productName.contains("MySQL", ignoreCase = true) -> {
                logger.info("🐬 Using MySQL cleanup strategy")
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")
                tables.forEach { table ->
                    logger.debug("🗑️ Truncating MySQL table: {}", table)
                    jdbcTemplate.execute("TRUNCATE TABLE $table")
                }
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")
            }
            else -> {
                logger.info("🔧 Using fallback cleanup strategy for: {}", productName)
                // Fallback: try ANSI-compliant truncate (may fail depending on DB)
                tables.forEach { table ->
                    logger.debug("🗑️ Truncating table: {}", table)
                    jdbcTemplate.execute("TRUNCATE TABLE $table")
                }
            }
        }

        logger.info("✅ Database cleanup completed successfully!")
    }
}