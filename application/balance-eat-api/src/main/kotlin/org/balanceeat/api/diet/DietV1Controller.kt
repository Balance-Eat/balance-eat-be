package org.balanceeat.api.diet

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/diets")
class DietV1Controller(
    private val dietService: DietService
) {

    @GetMapping("/daily")
    fun getDailyDiets(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam date: LocalDate,
    ): ApiResponse<List<DietV1Response.DietResponse>> {
        return ApiResponse.success(
            dietService.getDailyDiets(userId, date)
        )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    fun createDiet(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody @Valid request: DietV1Request.Create
    ): ApiResponse<DietV1Response.Details> {
        return ApiResponse.success(dietService.create(request, userId))
    }
}