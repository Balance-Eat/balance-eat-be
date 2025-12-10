package org.balanceeat.api.diet

import org.balanceeat.domain.diet.Diet
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DietScheduler(
    private val dietScheduleService: DietScheduleService,
) {
    private val log = LoggerFactory.getLogger(DietScheduler::class.java)

    @Scheduled(cron = "0 0 11,17,21 * * ?")
    fun aggregateDailyStats() {
        val targetDateTime = LocalDateTime.now()
        val mealType = when (targetDateTime.hour) {
            11 -> Diet.MealType.BREAKFAST
            17 -> Diet.MealType.LUNCH
            21 -> Diet.MealType.DINNER
            else -> {
                log.error("Invalid hour for diet notification: ${targetDateTime.hour}")
                return
            }
        }

        dietScheduleService.sendNotRegisteredDietNotifications(mealType, targetDateTime.toLocalDate())
    }
}