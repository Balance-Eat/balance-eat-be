package org.balanceeat.api.reminder

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.jackson.JsonUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

@WebMvcTest(ReminderV1Controller::class)
class ReminderV1ControllerTest : ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var reminderService: ReminderService

    @Nested
    @DisplayName("POST /v1/reminders - 리마인더 생성")
    inner class CreateTest {
        @Test
        fun success() {
            // given
            val request = reminderCreateV1RequestFixture()
            every { reminderService.create(any(), any()) } returns mockDetailsResponse()

            given()
                .header("X-USER-ID", "1")
                .body(JsonUtils.stringify(request))
                .post("/v1/reminders")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("생성")),
                        requestFields(
                            "content" type STRING means "리마인더 내용 (최대 500자)",
                            "sendTime" type STRING means "발송 시각 (HH:mm:ss 형식, 초는 00으로 정규화됨)",
                            "isActive" type BOOLEAN means "활성화 여부 (기본값: true)",
                            "dayOfWeeks" type ARRAY means "알림 요일 (1: 월요일 ~ 7: 일요일)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "생성된 리마인더 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.content" type STRING means "리마인더 내용",
                                "data.sendTime" type STRING means "발송 시각",
                                "data.isActive" type BOOLEAN means "활성화 여부",
                                "data.dayOfWeeks" type ARRAY means "알림 요일" withEnum DayOfWeek::class,
                                "data.createdAt" type STRING means "생성 시간",
                                "data.updatedAt" type STRING means "수정 시간"
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }
    }

    @Nested
    @DisplayName("GET /v1/reminders/{reminderId} - 리마인더 상세 조회")
    inner class GetDetailTest {
        @Test
        fun success() {
            // given
            every { reminderService.getDetail(any(), any()) } returns mockDetailsResponse()

            given()
                .header("X-USER-ID", "1")
                .get("/v1/reminders/{reminderId}", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDetailReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("상세 조회")),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "리마인더 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.content" type STRING means "리마인더 내용",
                                "data.sendTime" type STRING means "발송 시각",
                                "data.isActive" type BOOLEAN means "활성화 여부",
                                "data.dayOfWeeks" type ARRAY means "알림 요일" withEnum DayOfWeek::class,
                                "data.createdAt" type STRING means "생성 시간",
                                "data.updatedAt" type STRING means "수정 시간"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("PATCH /v1/reminders/{reminderId} - 리마인더 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            // given
            val request = reminderUpdateV1RequestFixture()
            every { reminderService.update(any(), any(), any()) } returns mockDetailsResponse()

            given()
                .header("X-USER-ID", "1")
                .body(JsonUtils.stringify(request))
                .put("/v1/reminders/{reminderId}", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("수정")),
                        requestFields(
                            "content" type STRING means "수정할 리마인더 내용 (최대 500자)",
                            "sendTime" type STRING means "수정할 발송 시각 (HH:mm:ss 형식)",
                            "isActive" type BOOLEAN means "활성화 여부",
                            "dayOfWeeks" type ARRAY means "알림 요일 (1: 월요일 ~ 7: 일요일)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "리마인더 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.content" type STRING means "수정된 리마인더 내용",
                                "data.sendTime" type STRING means "수정된 발송 시각",
                                "data.isActive" type BOOLEAN means "활성화 여부",
                                "data.dayOfWeeks" type ARRAY means "알림 요일" withEnum DayOfWeek::class,
                                "data.createdAt" type STRING means "생성 시간",
                                "data.updatedAt" type STRING means "수정 시간"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("DELETE /v1/reminders/{reminderId} - 리마인더 삭제")
    inner class DeleteTest {
        @Test
        fun success() {
            // given
            every { reminderService.delete(any(), any()) } returns Unit

            given()
                .header("X-USER-ID", "1")
                .delete("/v1/reminders/{reminderId}", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("DeleteReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("삭제"))
                    )
                )
                .status(HttpStatus.NO_CONTENT)
        }
    }

    @Nested
    @DisplayName("GET /v1/reminders - 리마인더 요약 목록 조회")
    inner class GetSummariesTest {
        @Test
        fun success() {
            // given
            every { reminderService.getSummaries(any(), any()) } returns mockPageResponse()

            given()
                .header("X-USER-ID", "1")
                .get("/v1/reminders")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetSummariesReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("요약 목록 조회")),
                        responseFields(
                            fieldsWithBasic(
                                "data.totalItems" type NUMBER means "전체 아이템 수",
                                "data.totalItems" type NUMBER means "전체 아이템 수",
                                "data.currentPage" type NUMBER means "현재 페이지 번호",
                                "data.itemsPerPage" type NUMBER means "페이지당 아이템 수",
                                "data.totalPages" type NUMBER means "전체 페이지 수",
                                "data.items" type ARRAY means "검색 결과 목록",
                                "data.items[].id" type NUMBER means "리마인더 ID",
                                "data.items[].content" type STRING means "리마인더 내용",
                                "data.items[].sendTime" type STRING means "발송 시각",
                                "data.items[].isActive" type BOOLEAN means "활성화 여부",
                                "data.items[].dayOfWeeks" type ARRAY means "알림 요일"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("PATCH /v1/reminders/{reminderId}/activation - 리마인더 활성화 상태 변경")
    inner class UpdateActivationTest {
        @Test
        fun success() {
            // given
            val request = reminderUpdateActivationV1RequestFixture()
            every { reminderService.updateActivation(any(), any(), any()) } returns mockDetailsResponse()

            given()
                .header("X-USER-ID", "1")
                .body(JsonUtils.stringify(request))
                .patch("/v1/reminders/{reminderId}/activation", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateActivationReminderTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.REMINDER.tagName)
                            .description(Tags.REMINDER.descriptionWith("활성화 상태 변경")),
                        requestFields(
                            "isActive" type BOOLEAN means "변경할 활성화 여부 (true: 활성화, false: 비활성화)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "리마인더 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.content" type STRING means "리마인더 내용",
                                "data.sendTime" type STRING means "발송 시각",
                                "data.isActive" type BOOLEAN means "변경된 활성화 여부",
                                "data.dayOfWeeks" type ARRAY means "알림 요일" withEnum DayOfWeek::class,
                                "data.createdAt" type STRING means "생성 시간",
                                "data.updatedAt" type STRING means "수정 시간"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }


    private fun mockDetailsResponse(): ReminderV1Response.Details {
        return ReminderV1Response.Details(
            id = 1L,
            userId = 1L,
            content = "아침 식사 기록하기",
            sendTime = LocalTime.of(9, 0, 0),
            isActive = true,
            dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            createdAt = LocalDateTime.of(2025, 12, 1, 10, 0, 0),
            updatedAt = LocalDateTime.of(2025, 12, 1, 10, 0, 0)
        )
    }

    private fun mockPageResponse(): PageResponse<ReminderV1Response.Summary> {
        val item = ReminderV1Response.Summary(
            id = 1L,
            content = "아침 식사 기록하기",
            sendTime = LocalTime.of(9, 0, 0),
            isActive = true,
            dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        )
        return PageResponse(
            totalItems = 1,
            currentPage = 0,
            itemsPerPage = 10,
            items = listOf(item)
        )
    }
}
