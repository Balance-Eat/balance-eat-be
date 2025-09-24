package org.balanceeat.domain.food

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.common.utils.CalorieCalculator
import org.springframework.transaction.annotation.Transactional

@DomainService
class FoodDomainService(
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getFood(foodId: Long): FoodDto {
        val food = foodRepository.findById(foodId)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }
        return FoodDto.from(food)
    }
    
    @Transactional
    fun create(command: FoodCommand.Create): FoodDto {
        val food = Food(
            name = command.name,
            uuid = command.uuid,
            userId = command.userId,
            servingSize = command.servingSize,
            perServingCalories = CalorieCalculator.calculate(
                carbohydrates = command.carbohydrates,
                protein = command.protein,
                fat = command.fat
            ),
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            brand = command.brand,
            isAdminApproved = command.isAdminApproved
        )
        
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
    
    @Transactional
    fun update(command: FoodCommand.Update): FoodDto {
        val food = foodRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }

        food.update(
            name = command.name,
            servingSize = command.servingSize,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            brand = command.brand
        )

        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
}