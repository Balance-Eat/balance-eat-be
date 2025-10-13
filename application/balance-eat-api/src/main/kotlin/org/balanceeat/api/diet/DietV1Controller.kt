package org.balanceeat.api.diet

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/v1/diets")
class DietV1Controller(
    private val dietService: DietService
) {

    @GetMapping("/daily")
    fun getDailyDiets(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam date: LocalDate,
    ): ApiResponse<List<DietV1Response.Summary>> {
        return ApiResponse.success(
            dietService.getDailyDiets(userId, date)
        )
    }

    @GetMapping("/monthly")
    fun getMonthlyDiets(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam yearMonth: YearMonth,
    ): ApiResponse<List<DietV1Response.Summary>> {
        return ApiResponse.success(
            dietService.getMonthlyDiets(userId, yearMonth)
        )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    fun create(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody @Valid request: DietV1Request.Create
    ): ApiResponse<DietV1Response.Details> {
        return ApiResponse.success(dietService.create(request, userId))
    }

    @PutMapping("/{dietId}")
    fun update(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable dietId: Long,
        @RequestBody @Valid request: DietV1Request.Update
    ): ApiResponse<DietV1Response.Details> {
        return ApiResponse.success(dietService.update(request, dietId, userId))
    }

    @DeleteMapping("/{dietId}")
    fun delete(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable dietId: Long
    ): ApiResponse<Void> {
        dietService.delete(dietId, userId)
        return ApiResponse.success()
    }

    @DeleteMapping("/{dietId}/diet-foods/{dietFoodId}")
    fun deleteDietFood(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable dietId: Long,
        @PathVariable dietFoodId: Long
    ): ApiResponse<Void> {
        dietService.deleteDietFood(dietId, dietFoodId, userId)
        return ApiResponse.success()
    }

    @PutMapping("/{dietId}/diet-foods/{dietFoodId}")
    fun updateDietFood(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable dietId: Long,
        @PathVariable dietFoodId: Long,
        @RequestBody @Valid request: DietV1Request.UpdateDietFood
    ): ApiResponse<DietV1Response.Details> {
        val result = dietService.updateDietFood(dietId, dietFoodId, request, userId)
        return ApiResponse.success(result)
    }
}