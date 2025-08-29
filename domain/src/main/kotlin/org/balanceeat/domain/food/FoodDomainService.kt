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
            name = command.name,
            uuid = command.uuid,
            userId = command.userId,
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
    
    @Transactional
    fun update(command: FoodCommand.Update): FoodDto {
        val food = foodRepository.findById(command.foodId)
            .orElseThrow { NotFoundException(DomainStatus.FOOD_NOT_FOUND) }
            
        // 작성자만 수정할 수 있음
        if (food.userId != command.userId) {
            throw IllegalArgumentException("음식을 수정할 권한이 없습니다")
        }
        
        val updatedFood = Food(
            id = food.id,
            name = command.name ?: food.name,
            uuid = food.uuid,
            userId = food.userId,
            perCapitaIntake = command.perCapitaIntake ?: food.perCapitaIntake,
            unit = command.unit ?: food.unit,
            carbohydrates = command.carbohydrates ?: food.carbohydrates,
            protein = command.protein ?: food.protein,
            fat = command.fat ?: food.fat,
            isVerified = command.isVerified ?: food.isVerified
        )
        
        val savedFood = foodRepository.save(updatedFood)
        return FoodDto.from(savedFood)
    }
}