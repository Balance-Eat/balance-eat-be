package org.balanceeat.api.reminder

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.jackson.JsonUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import java.time.LocalDateTime

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
                            "sendDatetime" type STRING means "발송 시각 (ISO 8601 형식, 초단위는 00으로 정규화됨)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "생성된 리마인더 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.content" type STRING means "리마인더 내용",
                                "data.sendDatetime" type STRING means "발송 시각",
                                "data.createdAt" type STRING means "생성 시간",
                                "data.updatedAt" type STRING means "수정 시간"
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }
    }

    private fun mockDetailsResponse(): ReminderV1Response.Details {
        return ReminderV1Response.Details(
            id = 1L,
            userId = 1L,
            content = "아침 식사 기록하기",
            sendDatetime = LocalDateTime.of(2025, 12, 2, 9, 0, 0),
            createdAt = LocalDateTime.of(2025, 12, 1, 10, 0, 0),
            updatedAt = LocalDateTime.of(2025, 12, 1, 10, 0, 0)
        )
    }
}
