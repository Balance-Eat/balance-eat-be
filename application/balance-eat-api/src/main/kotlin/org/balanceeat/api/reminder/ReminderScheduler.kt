package org.balanceeat.api.reminder

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReminderScheduler(
    private val reminderScheduleService: ReminderScheduleService
) {
    private val log = KotlinLogging.logger{}

    @Scheduled(cron = "40 */5 * * * *", zone = "Asia/Seoul")
    fun sendAll() {
        val targetDateTime = LocalDateTime.now().withSecond(0).withNano(0)
        log.info { "리마인더 전송 스케줄러 시작 - 대상 시간: $targetDateTime" }
        reminderScheduleService.sendAllBy(targetDateTime)
        log.info { "리마인더 전송 스케줄러 종료 - 대상 시간: $targetDateTime" }
    }
}