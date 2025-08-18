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
    
    fun searchFoods(keyword: String): List<Food> {
        return foodRepository.searchByKeyword(keyword)
    }
    
    fun getFoodsByCategory(category: String, verifiedOnly: Boolean = false): List<Food> {
        return if (verifiedOnly) {
            foodRepository.findVerifiedFoodsByCategory(category)
        } else {
            foodRepository.findByCategory(category)
        }
    }
    
    fun getFoodByBarcode(barcode: String): Food? {
        return foodRepository.findByBarcode(barcode)
    }
    
    @Transactional
    fun createFood(command: FoodCommand.Create): Food {
        val food = Food(
            name = command.name,
            brand = command.brand,
            servingSize = command.servingSize,
            servingUnit = command.servingUnit,
            caloriesPerServing = command.caloriesPerServing,
            carbohydratesPerServing = command.carbohydratesPerServing,
            proteinPerServing = command.proteinPerServing,
            fatPerServing = command.fatPerServing,
            sugarPerServing = command.sugarPerServing,
            sodiumPerServing = command.sodiumPerServing,
            fiberPerServing = command.fiberPerServing,
            description = command.description,
            barcode = command.barcode,
            category = command.category,
            isVerified = command.isVerified
        )
        
        return foodRepository.save(food)
    }
    
    @Transactional
    fun updateFood(foodId: Long, command: FoodCommand.Update): Food {
        val food = getFood(foodId)
        
        // Food entity is immutable, so we need to create a new one with updated values
        val updatedFood = Food(
            id = food.id,
            name = command.name ?: food.name,
            brand = command.brand ?: food.brand,
            servingSize = command.servingSize ?: food.servingSize,
            servingUnit = command.servingUnit ?: food.servingUnit,
            caloriesPerServing = command.caloriesPerServing ?: food.caloriesPerServing,
            carbohydratesPerServing = command.carbohydratesPerServing ?: food.carbohydratesPerServing,
            proteinPerServing = command.proteinPerServing ?: food.proteinPerServing,
            fatPerServing = command.fatPerServing ?: food.fatPerServing,
            sugarPerServing = command.sugarPerServing ?: food.sugarPerServing,
            sodiumPerServing = command.sodiumPerServing ?: food.sodiumPerServing,
            fiberPerServing = command.fiberPerServing ?: food.fiberPerServing,
            description = command.description ?: food.description,
            barcode = command.barcode ?: food.barcode,
            category = command.category ?: food.category,
            isVerified = command.isVerified ?: food.isVerified
        )
        
        return foodRepository.save(updatedFood)
    }
    
    @Transactional
    fun deleteFood(foodId: Long) {
        val food = getFood(foodId)
        foodRepository.delete(food)
    }
}