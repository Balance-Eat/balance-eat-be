package org.balanceeat.domain.stats

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.common.repository.BaseQueryRepository
import org.balanceeat.domain.common.repository.ExceededBulkLimitException
import org.balanceeat.domain.diet.StatsType
import org.springframework.jdbc.core.DataClassRowMapper
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

    override fun getStats(
        userId: Long,
        type: StatsType,
        from: LocalDate,
        to: LocalDate
    ): List<DietStatsAggregateResult> {
        val extractField = when (type) {
            StatsType.DAILY -> "DAY"
            StatsType.WEEKLY -> "WEEK"
            StatsType.MONTHLY -> "MONTH"
        }

        val sql = """
            SELECT user_id,
                   EXTRACT($extractField FROM stats_date) as group_key,
                   MIN(stats_date) as stats_date,
                   SUM(total_calories) as total_calories,
                   SUM(total_carbohydrates) as total_carbohydrates,
                   SUM(total_protein) as total_protein,
                   SUM(total_fat) as total_fat
            FROM diet_stats
            WHERE user_id = :userId
            AND stats_date BETWEEN :from AND :to
            GROUP BY user_id, group_key
            ORDER BY stats_date DESC
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("from", from)
            .addValue("to", to)

        return jdbcTemplate.query(
            sql,
            params,
            DataClassRowMapper.newInstance(DietStatsAggregateResult::class.java)
        )
    }
}
