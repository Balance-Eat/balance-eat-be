package org.balanceeat.api.food

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.food.FoodSearchResult
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodService: FoodService,
) {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: FoodV1Request.Create): ApiResponse<FoodV1Response.Details> {
        val result = foodService.create(request, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(result)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: FoodV1Request.Update): ApiResponse<FoodV1Response.Details> {
        val result = foodService.update(request, id, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getDetails(@PathVariable id: Long): ApiResponse<FoodV1Response.Details> {
        val result = foodService.getDetails(id)
        return ApiResponse.success(result)
    }

    @GetMapping("/recommendations")
    fun getRecommendations(@RequestParam(defaultValue = "10") limit: Int): ApiResponse<List<FoodV1Response.Details>> {
        val result = foodService.getRecommendations(limit)
        return ApiResponse.success(result)
    }

    @GetMapping("/search")
    fun search(
        request: FoodV1Request.Search,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<FoodSearchResult>> {
        val result = foodService.search(request, pageable)

        return ApiResponse.success(result)
    }
}