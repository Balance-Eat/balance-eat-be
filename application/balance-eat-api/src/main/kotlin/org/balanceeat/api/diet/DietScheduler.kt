package org.balanceeat.api.diet

import mu.KotlinLogging
import org.balanceeat.domain.diet.Diet
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DietScheduler(
    private val dietScheduleService: DietScheduleService,
) {
    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 11,17,21 * * ?")
    fun aggregateDailyStats() {


        val targetDateTime = LocalDateTime.now()
        log.info { "식단 미등록 알림 전송 스케줄러 시작 $targetDateTime" }
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

        log.info { "식단 미등록 알림 전송 스케줄러 종료 $targetDateTime" }
    }
}