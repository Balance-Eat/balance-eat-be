package org.balanceeat.api.reminder

import io.mockk.verify
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.apppush.AppPushHistoryRepository
import org.balanceeat.domain.apppush.NotificationDeviceRepository
import org.balanceeat.domain.apppush.notificationDeviceFixture
import org.balanceeat.domain.reminder.ReminderRepository
import org.balanceeat.domain.reminder.reminderFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class ReminderScheduleServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var reminderScheduleService: ReminderScheduleService

    @Autowired
    private lateinit var reminderRepository: ReminderRepository

    @Autowired
    private lateinit var notificationDeviceRepository: NotificationDeviceRepository

    @Autowired
    private lateinit var appPushHistoryRepository: AppPushHistoryRepository

    @Nested
    @DisplayName("특정 시간대 리마인더 전송 테스트")
    inner class SendAllByTest {

        @Test
        fun `특정 요일과 시간에 해당하는 활성 리마인더를 찾아 푸시 알림을 전송할 수 있다`() {
            // given
            val targetDateTime = LocalDateTime.of(2024, 1, 8, 9, 0, 0) // 월요일 09:00
            val targetTime = LocalTime.of(9, 0, 0)
            val userId1 = 100L
            val userId2 = 200L

            // 활성 리마인더 생성
            reminderRepository.saveAll(listOf(
                reminderFixture {
                    this.userId = userId1
                    this.dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
                    this.sendTime = targetTime
                    this.isActive = true
                    this.content = "아침 식사 기록하기"
                },
                reminderFixture {
                    this.userId = userId2
                    this.dayOfWeeks = listOf(DayOfWeek.MONDAY)
                    this.sendTime = targetTime
                    this.isActive = true
                    this.content = "점심 식사 기록하기"
                },
                // 비활성 리마인더 (전송되지 않아야 함)
                reminderFixture {
                    this.userId = userId1
                    this.dayOfWeeks = listOf(DayOfWeek.MONDAY)
                    this.sendTime = targetTime
                    this.isActive = false
                }
            ))

            // 알림 디바이스 생성
            val devices = notificationDeviceRepository.saveAll(listOf(
                notificationDeviceFixture {
                    this.userId = userId1
                    this.agentId = "device-agent-1"
                    this.isActive = true
                },
                notificationDeviceFixture {
                    this.userId = userId1
                    this.agentId = "device-agent-2"
                    this.isActive = true
                },
                notificationDeviceFixture {
                    this.userId = userId2
                    this.isActive = true
                }
            ))

            // when
            reminderScheduleService.sendAllBy(targetDateTime)

            // then
            verify(exactly = devices.size) { appPushSender.sendPushAsync(any()) }
        }
    }
}