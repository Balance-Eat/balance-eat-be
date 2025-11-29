package org.balanceeat.api.notification

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.apppush.NotificationDevice
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

@WebMvcTest(NotificationDeviceV1Controller::class)
class NotificationDeviceV1ControllerTest : ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var notificationDeviceService: NotificationDeviceService

    @Nested
    @DisplayName("POST /v1/notification-devices - 알림 디바이스 등록")
    inner class CreateTest {
        @Test
        fun success() {
            // given
            val request = mockCreateRequest()
            every { notificationDeviceService.create(any(), any()) } returns mockNotificationDeviceResponse()

            // when & then
            given()
                .header("X-USER-ID", "1")
                .body(request)
                .post("/v1/notification-devices")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.NOTIFICATION.tagName)
                            .description(Tags.NOTIFICATION.descriptionWith("디바이스 등록")),
                        requestHeaders(
                            headerWithName("X-USER-ID").description("사용자 ID")
                        ),
                        requestFields(
                            "agentId" type STRING means "디바이스 고유 식별자",
                            "osType" type STRING means "디바이스 운영체제 타입" withEnum NotificationDevice.OsType::class,
                            "deviceName" type STRING means "디바이스 이름",
                            "isActive" type BOOLEAN means "알림 수신 허용 여부"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "알림 디바이스 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.agentId" type STRING means "디바이스 고유 식별자",
                                "data.osType" type STRING means "디바이스 운영체제 타입" withEnum NotificationDevice.OsType::class,
                                "data.deviceName" type STRING means "디바이스 이름",
                                "data.isActive" type BOOLEAN means "알림 수신 허용 여부"
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }

        private fun mockCreateRequest(): NotificationDeviceV1Request.Create {
            return NotificationDeviceV1Request.Create(
                agentId = "test-agent-id-123",
                osType = NotificationDevice.OsType.AOS,
                deviceName = "갤럭시 S24",
                isActive = true
            )
        }
    }

    @Nested
    @DisplayName("GET /v1/notification-devices/current - 현재 디바이스 조회")
    inner class GetCurrentTest {
        @Test
        fun success() {
            // given
            every { notificationDeviceService.getCurrent(any(), any()) } returns mockNotificationDeviceResponse()

            // when & then
            given()
                .header("X-USER-ID", "1")
                .header("X-Device-Agent-Id", "test-agent-id-123")
                .get("/v1/notification-devices/current")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetCurrentTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.NOTIFICATION.tagName)
                            .description(Tags.NOTIFICATION.descriptionWith("현재 디바이스 조회")),
                        requestHeaders(
                            headerWithName("X-USER-ID").description("사용자 ID"),
                            headerWithName("X-Device-Agent-Id").description("디바이스 고유 식별자")
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "알림 디바이스 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.agentId" type STRING means "디바이스 고유 식별자",
                                "data.osType" type STRING means "디바이스 운영체제 타입" withEnum NotificationDevice.OsType::class,
                                "data.deviceName" type STRING means "디바이스 이름",
                                "data.isActive" type BOOLEAN means "알림 수신 허용 여부"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("PATCH /v1/notification-devices/{deviceId}/activation - 알림 활성화 상태 수정")
    inner class UpdateActivationTest {
        @Test
        fun success() {
            // given
            val request = mockUpdateActivationRequest()
            every { notificationDeviceService.updateActivation(any(), any(), any()) } returns mockNotificationDeviceResponse()

            // when & then
            given()
                .header("X-USER-ID", "1")
                .body(request)
                .patch("/v1/notification-devices/{deviceId}/activation", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateActivationTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.NOTIFICATION.tagName)
                            .description(Tags.NOTIFICATION.descriptionWith("알림 on/off 상태 수정")),
                        requestHeaders(
                            headerWithName("X-USER-ID").description("사용자 ID")
                        ),
                        requestFields(
                            "isActive" type BOOLEAN means "알림 수신 허용 여부"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "알림 디바이스 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.agentId" type STRING means "디바이스 고유 식별자",
                                "data.osType" type STRING means "디바이스 운영체제 타입" withEnum NotificationDevice.OsType::class,
                                "data.deviceName" type STRING means "디바이스 이름",
                                "data.isActive" type BOOLEAN means "알림 수신 허용 여부"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateActivationRequest(): NotificationDeviceV1Request.UpdateActivation {
            return NotificationDeviceV1Request.UpdateActivation(
                isActive = false
            )
        }
    }

    private fun mockNotificationDeviceResponse(): NotificationDeviceV1Response.Details {
        return NotificationDeviceV1Response.Details(
            id = 1L,
            userId = 1L,
            agentId = "test-agent-id-123",
            osType = NotificationDevice.OsType.AOS,
            deviceName = "갤럭시 S24",
            isActive = true
        )
    }
}
