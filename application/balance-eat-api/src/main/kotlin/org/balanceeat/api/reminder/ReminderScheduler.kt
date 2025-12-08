package org.balanceeat.api.reminder

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReminderScheduler(
    private val reminderScheduleService: ReminderScheduleService
) {

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    fun aggregateDailyStats() {
        val targetDateTime = LocalDateTime.now().withSecond(0).withNano(0)
        reminderScheduleService.sendAllBy(targetDateTime)
    }
}