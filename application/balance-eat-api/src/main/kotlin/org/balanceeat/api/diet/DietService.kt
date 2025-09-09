package org.balanceeat.api.diet

import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietCommand
import org.balanceeat.domain.diet.DietDomainService
import org.balanceeat.domain.user.UserDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DietService(
    private val dietDomainService: DietDomainService,
    private val userDomainService: UserDomainService
) {
    @Transactional
    fun create(request: DietV1Request.Create, userId: Long): DietV1Response.Details {
        userDomainService.validateExistsUser(userId)

        val command = DietCommand.Create(
            userId = userId,
            mealType = Diet.MealType.valueOf(request.mealType),
            consumedAt = request.consumedAt,
            foods = request.dietFoods.map { dietFood ->
                DietCommand.Create.Food(
                    foodId = dietFood.foodId,
                    intake = dietFood.intake
                )
            }
        )
        
        return DietV1Response.Details.from(
            dietDomainService.create(command)
        )
    }
}