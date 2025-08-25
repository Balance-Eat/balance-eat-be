package org.balanceeat.api.food

import jakarta.validation.Valid
import org.balanceeat.api.config.ApiResponse
import org.balanceeat.domain.food.FoodCommand
import org.balanceeat.domain.food.FoodDomainService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodDomainService: FoodDomainService,
) : FoodV1ApiSpec {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@RequestBody @Valid request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info> {
        val command = FoodCommand.Create(
            uuid = request.uuid,
            name = request.name,
            perCapitaIntake = request.perCapitaIntake,
            unit = request.unit,
            carbohydrates = request.carbohydrates,
            protein = request.protein,
            fat = request.fat
        )
        
        val result = foodDomainService.create(command)
        return ApiResponse.success(
            FoodV1Response.Info.from(result)
        )
    }

    @GetMapping("/{id}")
    override fun getFood(@PathVariable id: Long): ApiResponse<FoodV1Response.Info> {
        val food = foodDomainService.getFood(id)
        return ApiResponse.success(FoodV1Response.Info.from(food))
    }
}