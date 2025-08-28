package org.balanceeat.domain.food

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodDomainService(
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getFood(foodId: Long): FoodDto {
        val food = foodRepository.findById(foodId)
            .orElseThrow { NotFoundException(DomainStatus.FOOD_NOT_FOUND) }
        return FoodDto.from(food)
    }
    
    @Transactional
    fun create(command: FoodCommand.Create): FoodDto {
        val food = Food(
            uuid = command.uuid,
            name = command.name,
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            isVerified = command.isVerified
        )
        
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
}