package org.balanceeat.admin.api.food

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.admin.api.supports.ControllerTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import java.time.LocalDateTime

@WebMvcTest(AdminFoodV1Controller::class)
class AdminFoodV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean(relaxed = true)
    private lateinit var adminFoodService: AdminFoodService

    @Nested
    @DisplayName("PUT /admin/v1/foods/{id} - 어드민 음식 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            val request = mockUpdateRequest()
            every { adminFoodService.update(any(), any(), any()) } returns mockFoodDetailsResponse()

            given()
                .body(request)
                .put("/admin/v1/foods/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.ADMIN_FOOD.tagName)
                            .description(Tags.ADMIN_FOOD.descriptionWith("수정")),
                        pathParameters(
                            "id" pathMeans "음식 ID"
                        ),
                        requestFields(
                            "name" type STRING means "음식명",
                            "servingSize" type NUMBER means "1회 기준 섭취량",
                            "unit" type STRING means "단위 (예: g, ml 등)",
                            "carbohydrates" type NUMBER means "탄수화물 함량 (g)",
                            "protein" type NUMBER means "단백질 함량 (g)",
                            "fat" type NUMBER means "지방 함량 (g)",
                            "brand" type STRING means "브랜드명",
                            "isAdminApproved" type BOOLEAN means "관리자 승인 여부"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.userId" type NUMBER means "음식 등록자 ID",
                                "data.name" type STRING means "음식명",
                                "data.servingSize" type NUMBER means "1회 기준 섭취량",
                                "data.perServingCalories" type NUMBER means "1회 제공량 당 칼로리 (kcal)",
                                "data.unit" type STRING means "단위",
                                "data.carbohydrates" type NUMBER means "탄수화물 함량 (g)",
                                "data.protein" type NUMBER means "단백질 함량 (g)",
                                "data.fat" type NUMBER means "지방 함량 (g)",
                                "data.brand" type STRING means "브랜드명",
                                "data.isAdminApproved" type BOOLEAN means "관리자 승인 여부",
                                "data.createdAt" type STRING means "음식 등록일시"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateRequest(): AdminFoodV1Request.Update {
            return AdminFoodV1Request.Update(
                name = "수정된 음식",
                servingSize = 200.0,
                unit = "g",
                carbohydrates = 40.0,
                protein = 15.0,
                fat = 8.0,
                brand = "수정된 브랜드",
                isAdminApproved = true
            )
        }
    }

    @Nested
    @DisplayName("GET /admin/v1/foods/{id} - 어드민 음식 상세 조회")
    inner class GetDetailsTest {
        @Test
        fun success() {
            every { adminFoodService.getDetails(any()) } returns mockFoodDetailsResponse()

            given()
                .get("/admin/v1/foods/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDetailsTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.ADMIN_FOOD.tagName)
                            .description(Tags.ADMIN_FOOD.descriptionWith("상세 조회")),
                        pathParameters(
                            "id" pathMeans "음식 ID"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.userId" type NUMBER means "음식 등록자 ID",
                                "data.name" type STRING means "음식명",
                                "data.servingSize" type NUMBER means "1회 기준 섭취량",
                                "data.perServingCalories" type NUMBER means "1회 제공량 당 칼로리",
                                "data.unit" type STRING means "단위",
                                "data.carbohydrates" type NUMBER means "탄수화물 함량 (g)",
                                "data.protein" type NUMBER means "단백질 함량 (g)",
                                "data.fat" type NUMBER means "지방 함량 (g)",
                                "data.brand" type STRING means "브랜드명",
                                "data.isAdminApproved" type BOOLEAN means "관리자 승인 여부",
                                "data.createdAt" type STRING means "음식 등록일시"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    private fun mockFoodDetailsResponse(): AdminFoodV1Response.Details {
        return AdminFoodV1Response.Details(
            id = 1L,
            uuid = "test-uuid-123",
            name = "테스트 음식",
            userId = 1L,
            servingSize = 100.0,
            unit = "g",
            perServingCalories = 150.0,
            carbohydrates = 25.0,
            protein = 8.0,
            fat = 3.0,
            brand = "테스트 브랜드",
            isAdminApproved = true,
            createdAt = LocalDateTime.now()
        )
    }
}