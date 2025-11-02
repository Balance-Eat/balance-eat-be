package org.balanceeat.api.stats

import org.balanceeat.domain.diet.StatsType
import org.balanceeat.domain.stats.DietStatsRepository
import org.balanceeat.domain.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class StatsService(
    private val dietStatsRepository: DietStatsRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val BATCH_SIZE = 1000
    }

    @Transactional
    fun aggregateStats(statsDate: LocalDate) {
        dietStatsRepository.deleteByStatsDate(statsDate)

        val totalUserCount = userRepository.count()

        for (offset in 0 until totalUserCount step BATCH_SIZE.toLong()) {
            val pageRequest = PageRequest.of(offset.toInt() / BATCH_SIZE, BATCH_SIZE)
            val userIds = userRepository.findAll(pageRequest)
                .map { it.id }
                .toList()

            val aggregatedStats = dietStatsRepository.aggregate(statsDate, userIds)
            dietStatsRepository.createAll(aggregatedStats)
        }
    }

    @Transactional(readOnly = true)
    fun getStats(
        userId: Long,
        type: StatsType,
        from: LocalDate,
        to: LocalDate
    ): List<StatsV1Response.DietStats> {
        val statsList = dietStatsRepository.getStats(
                userId = userId,
                type = type,
                from = from,
                to = to
            )

        // 기간에 해당하는 모든 날짜 범위 생성
        val responseDateList = generatePeriods(type, from, to)

        // 각 기간별로 데이터가 있으면 사용하고, 없으면 0으로 초기화
        return responseDateList.map { date ->
            val statsForDate = statsList.find {
                it.statsDate == date ||
                (type == StatsType.WEEKLY && it.statsDate >= date && it.statsDate < date.plusWeeks(1)) ||
                (type == StatsType.MONTHLY && it.statsDate >= date && it.statsDate < date.plusMonths(1))
            }

            if (statsForDate != null) {
                StatsV1Response.DietStats.from(type, date, statsForDate)
            } else {
                StatsV1Response.DietStats.empty(type, date)
            }
        }
    }

    /**
     * 주어진 타입과 기간에 따라 모든 기간의 시작 날짜 목록을 생성
     */
    private fun generatePeriods(type: StatsType, from: LocalDate, to: LocalDate): List<LocalDate> {
        val periods = mutableListOf<LocalDate>()
        var current = from

        when (type) {
            StatsType.DAILY -> {
                while (!current.isAfter(to)) {
                    periods.add(current)
                    current = current.plusDays(1)
                }
            }
            StatsType.WEEKLY -> {
                // 주의 시작일(월요일)로 정규화
                current = from.with(java.time.DayOfWeek.MONDAY)
                while (!current.isAfter(to)) {
                    periods.add(current)
                    current = current.plusWeeks(1)
                }
            }
            StatsType.MONTHLY -> {
                // 월의 시작일(1일)로 정규화
                current = from.withDayOfMonth(1)
                while (!current.isAfter(to)) {
                    periods.add(current)
                    current = current.plusMonths(1)
                }
            }
        }

        return periods
    }
}