package org.balanceeat.domain.diet

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.food.FoodRepository
import org.springframework.transaction.annotation.Transactional

@DomainService
class DietDomainService(
    private val dietRepository: DietRepository,
    private val foodRepository: FoodRepository
) {
    @Transactional
    fun create(command: DietCommand.Create): DietDto {
        checkDuplication(command)

        val foodIds = command.dietFoods.map { it.foodId }
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }

        val diet = Diet(
            userId = command.userId,
            mealType = command.mealType,
            consumedAt = command.consumedAt
        )

        command.dietFoods.forEach { foodInfo ->
            val food = foodMap[foodInfo.foodId]
                ?: throw EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND)
            diet.addFood(food, foodInfo.intake)
        }
        
        val savedDiet = dietRepository.save(diet)

        return DietDto.from(savedDiet, foodMap)
    }

    private fun checkDuplication(command: DietCommand.Create) {
        val isDuplicate = dietRepository.existsByUserIdAndDateAndMealType(
            command.userId,
            command.consumedAt.toLocalDate(),
            command.mealType
        )

        if (isDuplicate) {
            throw DomainException(DomainStatus.DIET_MEAL_TYPE_ALREADY_EXISTS)
        }
    }
}