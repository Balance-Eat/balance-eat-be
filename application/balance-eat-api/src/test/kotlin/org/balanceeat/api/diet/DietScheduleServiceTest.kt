package org.balanceeat.api.diet

import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.apppush.NotificationDeviceRepository
import org.balanceeat.domain.apppush.notificationDeviceFixture
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.diet.dietFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DietScheduleServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var dietScheduleService: DietScheduleService

    @Autowired
    private lateinit var dietRepository: DietRepository

    @Autowired
    private lateinit var notificationDeviceRepository: NotificationDeviceRepository

    @Test
    @DisplayName("식단 미등록 사용자에게 알림을 전송한다")
    fun `성공`() {
        // given
        val targetDate = LocalDate.now()
        val mealType = Diet.MealType.BREAKFAST

        val user1 = 1L
        val user2 = 2L
        val user3 = 3L

        dietRepository.saveAll(listOf(
            dietFixture {
                userId = user1
                this.mealType = mealType
                consumedAt = LocalDateTime.of(targetDate, LocalTime.of(9, 0))
            }
        ))

        val devices = notificationDeviceRepository.saveAll(listOf(
            notificationDeviceFixture {
                userId = user2
                agentId = "device-user2-1"
                isActive = true
            },
            notificationDeviceFixture {
                userId = user2
                agentId = "device-user2-2"
                isActive = true
            },
            notificationDeviceFixture {
                userId = user3
                agentId = "device-user3-1"
                isActive = true
            }
        ))

        // when
        dietScheduleService.sendNotRegisteredDietNotifications(mealType, targetDate)

        // then
        verify(exactly = devices.size) { appPushSender.sendPushAsync(any()) }
    }
}
