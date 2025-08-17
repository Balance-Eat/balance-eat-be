package org.balanceeat.domain.diet

import org.balanceeat.domain.common.ErrorStatus
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.user.UserDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class DietDomainService(
    private val dietRepository: DietRepository,
    private val dietFoodRepository: DietFoodRepository,
    private val userDomainService: UserDomainService,
    private val foodDomainService: FoodDomainService
) {
    
    @Transactional
    fun create(command: DietCommand.Create): Diet {
        // 사용자 존재 확인
        userDomainService.findById(command.userId)
        
        val diet = Diet(
            userId = command.userId,
            mealType = command.mealType,
            mealDate = command.mealDate,
            consumedAt = command.consumedAt
        )
        
        val savedDiet = dietRepository.save(diet)
        
        // 음식 추가
        command.foods.forEach { addFoodCommand ->
            savedDiet.addFood(addFoodCommand.foodId, addFoodCommand.actualServingSize, addFoodCommand.servingUnit, foodDomainService)
        }
        
        return dietRepository.save(savedDiet)
    }
    
    @Transactional
    fun addFoodToDiet(dietId: Long, command: DietCommand.AddFood): DietFood {
        val diet = getDiet(dietId)
        val dietFood = diet.addFood(command.foodId, command.actualServingSize, command.servingUnit, foodDomainService)
        dietRepository.save(diet)
        return dietFood
    }
    
    @Transactional
    fun removeFoodFromDiet(command: DietCommand.RemoveFood) {
        val diet = getDiet(command.dietId)
        val dietFood = getDietFood(command.dietFoodId)
        
        diet.removeFood(dietFood)
        dietRepository.save(diet)
    }
    
    @Transactional
    fun updateDietFood(command: DietCommand.UpdateFood): DietFood {
        val dietFood = getDietFood(command.dietFoodId)
        val diet = getDiet(dietFood.dietId)
        val food = foodDomainService.getFood(dietFood.foodId)
        
        val nutritionInfo = food.calculateNutrition(command.actualServingSize)
        
        val updatedDietFood = DietFood(
            id = dietFood.id,
            dietId = dietFood.dietId,
            foodId = dietFood.foodId,
            actualServingSize = command.actualServingSize,
            servingUnit = command.servingUnit,
            calculatedCalories = nutritionInfo.calories,
            calculatedCarbohydrates = nutritionInfo.carbohydrates,
            calculatedProtein = nutritionInfo.protein,
            calculatedFat = nutritionInfo.fat,
            calculatedSugar = nutritionInfo.sugar,
            calculatedSodium = nutritionInfo.sodium,
            calculatedFiber = nutritionInfo.fiber
        )
        
        // Update the item in the diet's collection
        diet.removeFood(dietFood)
        diet.dietFoods.add(updatedDietFood)
        dietRepository.save(diet)
        
        return updatedDietFood
    }
    
    @Transactional(readOnly = true)
    fun getDiet(dietId: Long): Diet {
        return dietRepository.findById(dietId)
            .orElseThrow { NotFoundException(ErrorStatus.DIET_NOT_FOUND) }
    }
    
    @Transactional(readOnly = true)
    fun getDietFood(dietFoodId: Long): DietFood {
        return dietFoodRepository.findById(dietFoodId)
            .orElseThrow { NotFoundException(ErrorStatus.DIET_FOOD_NOT_FOUND) }
    }
    
    @Transactional(readOnly = true)
    fun findByUserIdAndDate(userId: Long, date: LocalDate): List<Diet> {
        return dietRepository.findByUserIdAndMealDateOrderByMealTypeAndCreatedAt(userId, date)
    }
    
    @Transactional(readOnly = true)
    fun findByUserUuidAndDate(userUuid: String, date: LocalDate): List<Diet> {
        val user = userDomainService.findByUuid(userUuid)
        return findByUserIdAndDate(user.id, date)
    }
    
    @Transactional(readOnly = true)
    fun search(command: DietCommand.Search): List<Diet> {
        return when {
            command.userId != null -> findByUserIdAndDate(command.userId, command.mealDate)
            command.userUuid != null -> findByUserUuidAndDate(command.userUuid, command.mealDate)
            else -> emptyList()
        }
    }
    
    @Transactional(readOnly = true)
    fun getDietFoodsByUserAndDate(userId: Long, date: LocalDate): List<DietFood> {
        return dietFoodRepository.findByUserIdAndMealDate(userId, date)
    }
    
    @Transactional(readOnly = true)
    fun getDietFoodsByUserAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<DietFood> {
        return dietFoodRepository.findByUserIdAndDateRange(userId, startDate, endDate)
    }
    
    @Transactional(readOnly = true)
    fun getDietTotalCalories(dietId: Long): BigDecimal {
        val diet = getDiet(dietId)
        return diet.getTotalCalories()
    }
    
    @Transactional(readOnly = true)
    fun getDietTotalNutrition(dietId: Long): NutritionSummary {
        val diet = getDiet(dietId)
        
        return NutritionSummary(
            calories = diet.getTotalCalories(),
            carbohydrates = diet.getTotalCarbohydrates(),
            protein = diet.getTotalProtein(),
            fat = diet.getTotalFat(),
            sugar = diet.getTotalSugar(),
            sodium = diet.getTotalSodium(),
            fiber = diet.getTotalFiber()
        )
    }
    
    data class NutritionSummary(
        val calories: BigDecimal,
        val carbohydrates: BigDecimal,
        val protein: BigDecimal,
        val fat: BigDecimal,
        val sugar: BigDecimal,
        val sodium: BigDecimal,
        val fiber: BigDecimal
    )
}