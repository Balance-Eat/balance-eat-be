package org.balanceeat.api.diet

import org.balanceeat.api.config.ApiResponse
import org.balanceeat.domain.diet.DietCommand
import org.balanceeat.domain.diet.DietDomainService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/diets")
class DietV1Controller(
    private val dietDomainService: DietDomainService
) : DietV1ApiSpec {
    
    @GetMapping
    override fun getDietsByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) userUuid: String?
    ): ApiResponse<List<DietV1Response.Info>> {
        
        val searchCommand = DietCommand.Search(
            userId = userId,
            userUuid = userUuid,
            mealDate = date
        )
        
        val diets = dietDomainService.search(searchCommand)
        
        val response = diets.map { diet ->
            // For now, return simplified data structure since we changed the entity model
            // TODO: Update to handle multiple foods per diet
            val totalNutrition = dietDomainService.getDietTotalNutrition(diet.id)
            
            DietV1Response.Info(
                id = diet.id,
                userId = diet.userId,
                mealType = diet.mealType,
                mealTypeName = diet.mealType.displayName,
                foodName = "Multiple Foods", // TODO: Aggregate food names
                foodBrand = null,
                servingSize = 0.0, // TODO: Aggregate serving sizes
                servingUnit = "mixed",
                calories = totalNutrition.calories,
                carbohydrates = totalNutrition.carbohydrates,
                protein = totalNutrition.protein,
                fat = totalNutrition.fat,
                sugar = totalNutrition.sugar,
                sodium = totalNutrition.sodium,
                fiber = totalNutrition.fiber,
                mealDate = diet.mealDate,
                consumedAt = diet.consumedAt,
                createdAt = diet.createdAt
            )
        }
        
        return ApiResponse.success(response)
    }
}