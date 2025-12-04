package org.balanceeat.api.reminder

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.reminder.ReminderRepository
import org.balanceeat.domain.reminder.reminderFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var reminderService: ReminderService

    @Autowired
    private lateinit var reminderRepository: ReminderRepository

    @Nested
    @DisplayName("리마인더 등록")
    inner class CreateTest {

        @Test
        fun `리마인더를 성공적으로 등록할 수 있다`() {
            // given
            val userId = 1L
            val request = reminderCreateV1RequestFixture()

            // when
            val result = reminderService.create(request, userId)

            // then
            assertThat(request)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }

    @Nested
    @DisplayName("리마인더 상세 조회")
    inner class GetDetailTest {

        @Test
        fun `리마인더를 성공적으로 조회할 수 있다`() {
            // given
            val userId = 1L
            val reminder = createEntity(
                reminderFixture {
                    this.userId = userId
                    content = "아침 식사 기록하기"
                    sendTime = LocalTime.of(9, 0, 0)
                    isActive = true
                    dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
                }
            )

            // when
            val result = reminderService.getDetail(reminder.id, userId)

            // then
            assertThat(result.id).isEqualTo(reminder.id)
            assertThat(result.userId).isEqualTo(userId)
            assertThat(result.content).isEqualTo(reminder.content)
            assertThat(result.sendTime).isEqualTo(reminder.sendTime)
            assertThat(result.isActive).isEqualTo(reminder.isActive)
            assertThat(result.dayOfWeeks).isEqualTo(reminder.dayOfWeeks)
        }

        @Test
        fun `다른 사용자의 리마인더는 조회할 수 없다`() {
            // given
            val ownerId = 1L
            val otherUserId = 2L
            val reminder = createEntity(reminderFixture { userId = ownerId })

            // when
            val throwable = catchThrowable {
                reminderService.getDetail(reminder.id, otherUserId)
            }

            // then
            assertThat(throwable).isInstanceOf(NotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.REMINDER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("리마인더 수정")
    inner class UpdateTest {

        @Test
        fun `리마인더를 성공적으로 수정할 수 있다`() {
            // given
            val userId = 1L
            val reminder = createEntity(
                reminderFixture {
                    this.userId = userId
                    content = "수정 전 내용"
                    sendTime = LocalTime.of(9, 0, 0)
                    isActive = true
                    dayOfWeeks = listOf(DayOfWeek.MONDAY)
                }
            )

            val request = reminderUpdateV1RequestFixture {
                content = "수정 후 내용"
                sendTime = LocalTime.of(10, 0, 0)
                isActive = false
                dayOfWeeks = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY) // 화, 목
            }

            // when
            val result = reminderService.update(request, reminder.id, userId)

            // then
            assertThat(result.content).isEqualTo(request.content)
            assertThat(result.sendTime).isEqualTo(request.sendTime)
            assertThat(result.isActive).isEqualTo(request.isActive)
            assertThat(result.dayOfWeeks).isEqualTo(request.dayOfWeeks)
        }

        @Test
        fun `다른 사용자의 리마인더는 수정할 수 없다`() {
            // given
            val ownerId = 1L
            val otherUserId = 2L
            val reminder = createEntity(reminderFixture { userId = ownerId })

            val request = reminderUpdateV1RequestFixture {}

            // when
            val throwable = catchThrowable {
                reminderService.update(request, reminder.id, otherUserId)
            }

            // then
            assertThat(throwable).isInstanceOf(NotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.REMINDER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("리마인더 삭제")
    inner class DeleteTest {

        @Test
        fun `리마인더를 성공적으로 삭제할 수 있다`() {
            // given
            val userId = 1L
            val reminder = createEntity(reminderFixture())

            // when
            reminderService.delete(reminder.id, userId)

            // then
            assertThat(reminderRepository.findById(reminder.id)).isEmpty
        }

        @Test
        fun `다른 사용자의 리마인더는 삭제할 수 없다`() {
            // given
            val ownerId = 1L
            val otherUserId = 2L
            val reminder = createEntity(reminderFixture { userId = ownerId })

            // when
            val throwable = catchThrowable {
                reminderService.delete(reminder.id, otherUserId)
            }

            // then
            assertThat(throwable).isInstanceOf(NotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.REMINDER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("리마인더 활성화 상태 변경")
    inner class UpdateActivationTest {

        @Test
        fun `리마인더 활성화 상태를 변경할 수 있다`() {
            // given
            val userId = 1L
            val reminder = createEntity(
                reminderFixture {
                    this.userId = userId
                    isActive = true
                }
            )

            val request = reminderUpdateActivationV1RequestFixture {
                isActive = false
            }

            // when
            val result = reminderService.updateActivation(request, reminder.id, userId)

            // then
            assertThat(result.isActive).isFalse()
            assertThat(result.id).isEqualTo(reminder.id)
        }

        @Test
        fun `비활성화된 리마인더를 활성화할 수 있다`() {
            // given
            val userId = 1L
            val reminder = createEntity(
                reminderFixture {
                    this.userId = userId
                    isActive = false
                }
            )

            val request = reminderUpdateActivationV1RequestFixture {
                isActive = true
            }

            // when
            val result = reminderService.updateActivation(request, reminder.id, userId)

            // then
            assertThat(result.isActive).isTrue()
            assertThat(result.id).isEqualTo(reminder.id)
        }

        @Test
        fun `다른 사용자의 리마인더 활성화 상태는 변경할 수 없다`() {
            // given
            val ownerId = 1L
            val otherUserId = 2L
            val reminder = createEntity(reminderFixture { userId = ownerId })

            val request = reminderUpdateActivationV1RequestFixture {}

            // when
            val throwable = catchThrowable {
                reminderService.updateActivation(request, reminder.id, otherUserId)
            }

            // then
            assertThat(throwable).isInstanceOf(NotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.REMINDER_NOT_FOUND)
        }
    }
}