package org.balanceeat.domain.reminder

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.common.repository.BaseQueryRepository
import org.balanceeat.jackson.ObjectMapperFactory
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowCallbackHandler
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderRepositoryCustomImpl (
    queryFactory: JPAQueryFactory,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
): BaseQueryRepository(queryFactory), ReminderRepositoryCustom {
    private val objectMapper = ObjectMapperFactory.getGlobalObjectMapper()

    // json 연산자로 인해 QueryDsl로 처리하기 어려워 jdbcTemplate 사용
    override fun findAllActiveBy(dayOfWeek: DayOfWeek, time: LocalTime, pageable: Pageable): List<Reminder> {
        val sql = """
            SELECT * FROM reminder
            WHERE day_of_weeks::jsonb @> :dayOfWeek::jsonb
            AND send_time = :sendTime
            AND is_active = true
            ORDER BY id
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val params = mapOf(
            "dayOfWeek" to "\"${dayOfWeek.name}\"",
            "sendTime" to time,
            "limit" to pageable.pageSize,
            "offset" to pageable.offset
        )

        return jdbcTemplate.query(sql, params) { rs, _ ->
            val reminder = Reminder(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                content = rs.getString("content"),
                sendTime = rs.getTime("send_time").toLocalTime(),
                isActive = rs.getBoolean("is_active"),
                dayOfWeeks = objectMapper.readValue(rs.getString("day_of_weeks"), Array<DayOfWeek>::class.java).toList()
            )

            reminder.createdAt = rs.getTimestamp("created_at").toLocalDateTime()
            reminder.updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()

            reminder
        }
    }

    // json 연산자로 인해 QueryDsl로 처리하기 어려워 jdbcTemplate 사용
    override fun countAllActiveBy(dayOfWeek: DayOfWeek, time: LocalTime): Long {
        val sql = """
            SELECT COUNT(*) FROM reminder
            WHERE day_of_weeks::jsonb @> :dayOfWeek::jsonb
            AND send_time = :sendTime
            AND is_active = true
        """.trimIndent()

        val params = mapOf(
            "dayOfWeek" to "\"${dayOfWeek.name}\"",
            "sendTime" to time
        )

        return jdbcTemplate.queryForObject(sql, params, Long::class.java) ?: 0L
    }
}

