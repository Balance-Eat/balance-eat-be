package org.balanceeat.api.diet

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/diets")
class DietV1Controller(
    private val dietService: DietService
) : DietV1ApiSpec {

    @GetMapping("/daily")
    override fun getDietsByDate(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDate
    ): ApiResponse<DietV1Response.DailyDietInfo> {
        // Mock data
        val mockDietItems = listOf(
            DietV1Response.DietItem(
                foodId = 101,
                foodName = "닭가슴살",
                intake = 100,
                unit = "g",
                calories = 120,
                carbohydrates = 0,
                protein = 23,
                fat = 1
            ),
            DietV1Response.DietItem(
                foodId = 102,
                foodName = "샐러드",
                intake = 100,
                unit = "g",
                calories = 25,
                carbohydrates = 4,
                protein = 2,
                fat = 0
            )
        )

        val mockDiet = DietV1Response.Diet(
            dietId = 1,
            eatingAt = "2025-08-04T20:15:00Z",
            type = "저녁",
            items = mockDietItems
        )

        val mockDailyTotal = DietV1Response.DailyTotal(
            totalCalorie = 145,
            totalCarbohydrates = 4,
            totalProtein = 25,
            totalFat = 1
        )

        val response = DietV1Response.DailyDietInfo(
            dailyTotal = mockDailyTotal,
            diets = listOf(mockDiet)
        )

        return ApiResponse.success(response, "성공적으로 조회되었습니다.")
    }
    
    @PostMapping
    @ResponseStatus(CREATED)
    override fun createDiet(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody @Valid request: DietV1Request.Create
    ): ApiResponse<DietV1Response.Details> {
        return ApiResponse.success(dietService.create(request, userId))
    }
}