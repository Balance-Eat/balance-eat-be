package org.balanceeat.domain.food

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.BadCommandException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
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
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            isAdminApproved = command.isAdminApproved
        )
        
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
    
    @Transactional
    fun update(command: FoodCommand.Update): FoodDto {
        val food = foodRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }

        if (food.userId != command.modifierId) {
            throw BadCommandException(DomainStatus.CANNOT_MODIFY_FOOD)
        }

        food.update(
            name = command.name,
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat
        )

        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }

    @Transactional
    fun updateByAdmin(command: FoodCommand.UpdateByAdmin): FoodDto {
        val food = foodRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }

        food.update(
            name = command.name,
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat
        )
        if (command.isAdminApproved) food.approve()

        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
}