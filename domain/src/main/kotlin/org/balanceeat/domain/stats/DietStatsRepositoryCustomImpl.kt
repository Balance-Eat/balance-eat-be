package org.balanceeat.domain.stats

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.common.repository.BaseQueryRepository
import org.balanceeat.domain.common.repository.ExceededBulkLimitException
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.time.LocalDate
import java.time.LocalDateTime

class DietStatsRepositoryCustomImpl(
    queryFactory: JPAQueryFactory,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) : BaseQueryRepository(queryFactory), DietStatsRepositoryCustom {
    private val batchInsert = SimpleJdbcInsert(jdbcTemplate.jdbcTemplate)
        .withTableName("diet_stats")
        .usingGeneratedKeyColumns("id")

    override fun aggregate(statsDate: LocalDate, userIds: List<Long>): List<DietStats> {
        val sql = """
            SELECT
                d.user_id,
                :statsDate as stats_date,
                SUM(f.per_serving_calories * df.intake / f.serving_size) as total_calories,
                SUM(f.carbohydrates * df.intake / f.serving_size) as total_carbohydrates,
                SUM(f.protein * df.intake / f.serving_size) as total_protein,
                SUM(f.fat * df.intake / f.serving_size) as total_fat
            FROM diet d
            INNER JOIN diet_food df ON d.id = df.diet_id
            INNER JOIN food f ON df.food_id = f.id
            WHERE d.user_id IN (:userIds)
            AND DATE(d.consumed_at) = :statsDate
            GROUP BY d.user_id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("statsDate", statsDate)
            .addValue("userIds", userIds)

        return jdbcTemplate.query(sql, params) { rs, _ ->
            DietStats(
                userId = rs.getLong("user_id"),
                statsDate = rs.getObject("stats_date", LocalDate::class.java),
                totalCalories = rs.getDouble("total_calories"),
                totalCarbohydrates = rs.getDouble("total_carbohydrates"),
                totalProtein = rs.getDouble("total_protein"),
                totalFat = rs.getDouble("total_fat")
            )
        }
    }

    override fun createAll(dietStatsList: List<DietStats>) {
        if (dietStatsList.size >= BULK_SIZE_LIMIT) {
            throw ExceededBulkLimitException(BULK_SIZE_LIMIT)
        }

        val now = LocalDateTime.now()

        dietStatsList.forEach { dietStats ->
            dietStats.createdAt = now
            dietStats.updatedAt = now
        }

        val batch = dietStatsList
            .map { BeanPropertySqlParameterSource(it) }
            .toTypedArray()

        batchInsert.executeBatch(*batch)
    }
}
