package org.balanceeat.domain.food

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FoodDomainService(
    private val foodRepository: FoodRepository
) {
    
    fun getFood(foodId: Long): Food {
        return foodRepository.findById(foodId)
            .orElseThrow { NotFoundException(DomainStatus.FOOD_NOT_FOUND) }
    }
    
    @Transactional
    fun create(command: FoodCommand.Create): Food {
        val food = Food(
            name = command.name,
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat
        )
        
        return foodRepository.save(food)
    }
}