package org.balanceeat.api.food

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodService: FoodService,
) : FoodV1ApiSpec {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@RequestBody @Valid request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info> {
        val result = foodService.create(request, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(
            FoodV1Response.Info.from(result)
        )
    }

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: FoodV1Request.Update): ApiResponse<FoodV1Response.Info> {
        val result = foodService.update(request, id, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(
            FoodV1Response.Info.from(result)
        )
    }

    @GetMapping("/{id}")
    override fun getDetails(@PathVariable id: Long): ApiResponse<FoodV1Response.Info> {
        val food = foodService.getDetails(id)
        return ApiResponse.success(FoodV1Response.Info.from(food))
    }

    @GetMapping("/recommendations")
    fun getRecommendations(@RequestParam(defaultValue = "10") limit: Int): ApiResponse<List<FoodV1Response.Info>> {
        val result = foodService.getRecommendations(limit)
            .map { FoodV1Response.Info.from(it) }

        return ApiResponse.success(result)
    }
}