package org.balanceeat.domain.diet

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.BadCommandException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.food.Food
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DomainService
class DietDomainService(
    private val dietRepository: DietRepository,
    private val foodRepository: FoodRepository
) {
    @Transactional
    fun create(command: DietCommand.Create): DietDto {
        val foodIds = command.foods.map { it.foodId }
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }

        val diet = Diet(
            userId = command.userId,
            mealType = command.mealType,
            consumedAt = command.consumedAt
        )

        command.foods.forEach { foodInfo ->
            val food = foodMap[foodInfo.foodId]
                ?: throw EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND)
            diet.addFood(food, foodInfo.intake)
        }
        
        val savedDiet = dietRepository.save(diet)

        return DietDto.from(savedDiet, foodMap)
    }
}